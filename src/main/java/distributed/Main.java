package distributed;


import clojure.java.api.Clojure;
import clojure.lang.*;
import com.google.gson.Gson;
import distributed.client.ControlThread;

import java.util.ArrayList;
import java.util.List;

public class Main {

    static {
        // 在尝试访问非核心 Clojure 命名空间前，需要先载入它。
        ClojureKt.getRequireFn().invoke(Symbol.intern("me.juhezi.creator"));
        ClojureKt.getRequireFn().invoke(Symbol.intern("me.juhezi.core"));
    }

    private static volatile String json = "";

    private static int currentGeneration = 0;   // 当前代数
    private static List<List> data = new ArrayList<>();

    public static void main(String[] args) {
        // todo 从文件中读取配置参数
        Config config = new Config();
        ControlThread controlThread = new ControlThread();
        controlThread.getConfig().setCallback(element -> {
            if (GlobalKt.getKEY_ERROR().equals(element.getOperation())) {
                // 进行本地计算，不进行分布式计算
                GlobalKt.dprintln("进行本地计算，不进行分布式计算");
                String result = ClientLauncherKt.calculate(json, config.getChromosome_size());
//                System.out.println(result);
                // todo 这里应该是对数据进行存储【新的线程】
                // 然后进行后面的进化操作
                List<List> subData = GlobalKt.getGson().fromJson(result, List.class);
                data.clear();
                data.addAll(subData);
                GlobalKt.dprintln("开始进化");
                String temp = singleEvolve(data, config.getSize(), config.getChromosome_size(),
                        config.getCrossover_probability(),
                        config.getMutation_probability());
                data.clear();
            } else if (GlobalKt.getKEY_RESULT().equals(element.getOperation())) {
                GlobalKt.dprintln("一个计算进程计算适应度完成，开始存储数据");
                // todo 检测是否所有的个体适应度都计算完全,如果是的话，就开始进行进化操作
                List<List> subData = GlobalKt.getGson().fromJson(element.getData(), List.class);
                data.addAll(subData);   // 添加数据
                if (data.size() >= config.getSize()) {  // 所有个体适应度计算完毕
                    GlobalKt.dprintln("所有个体适应度计算完毕，开始进化");
                    String result = singleEvolve(data, config.getSize(), config.getChromosome_size(),
                            config.getCrossover_probability(),
                            config.getMutation_probability());
                    data.clear();
                }
            }
            return null;
        });
        controlThread.start();
        // 调用 create() 方法生成随机数据
        IFn creater = Clojure.var("me.juhezi.creator", "create");
        // 生成随机种群，即初代个体
        LazySeq result = (LazySeq) creater.invoke(config.getChromosome_size(), config.getSize());
        // 把数据转换为字符串
        // 然后就是计算个体适应度

        List<PersistentVector> list = result.subList(0, result.size());
        json = GlobalKt.getGson().toJson(list);
        GlobalKt.dprintln("把数据发送到服务器，进行分布式运算");
        System.out.println("json " + json);
        controlThread.send(json);
    }

    /**
     * 种群进化，仅一代
     * <p>
     * data size chromosome_size crossover_probability mutation_probability
     *
     * @param data
     * @return
     */
    private static String singleEvolve(List<List> data, int size, int chromosome_size,
                                       double crossover_probability, double mutation_probability) {
        Object object = ClojureKt.getEvolve().invoke(data, size, chromosome_size, crossover_probability, mutation_probability);
        System.out.println(object);
        return "";
    }

}

// (me.juhezi.creator/create 25 10)
