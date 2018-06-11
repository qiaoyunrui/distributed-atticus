package distributed;


import clojure.java.api.Clojure;
import clojure.lang.*;
import distributed.client.ControlThread;

import java.util.ArrayList;
import java.util.List;

public class Main {

    static {
        // 在尝试访问非核心 Clojure 命名空间前，需要先载入它。
        ClojureKt.getRequireFn().invoke(Symbol.intern("me.juhezi.creator"));
        ClojureKt.getRequireFn().invoke(Symbol.intern("me.juhezi.core"));
    }

    private static double fitness = 0;
    private static double x = 0;
    private static double y = 0;

    private static volatile String json = "";

    private static int currentGeneration = 0;   // 当前代数
    // 进化前的数据，刚刚计算完适应度
    private static List<List> data = new ArrayList<>();
    // 进化后的数据，即下一代数据
    private static List<List> nextGeneration = new ArrayList<>();
    private static ControlThread controlThread = new ControlThread();
    private static Config config = new Config();

    public static void main(String[] args) {
        // todo 从文件中读取配置参数
        controlThread.getConfig().setCallback(element -> {
            if (GlobalKt.getKEY_ERROR().equals(element.getOperation())) {
                // 进行本地计算，不进行分布式计算
                GlobalKt.dprintln("进行本地计算，不进行分布式计算");
                String result = ClientLauncherKt.calculate(json, config.getChromosome_size());
                // todo 这里应该是对数据进行存储【新的线程】
                // 是否进行接下来的进化操作
                if (hasNext(config)) {
                    // 进化并计算种群适应度
                    List<List> subData = GlobalKt.getGson().fromJson(result, List.class);
                    data.addAll(subData);
                    updateBestFitness();
                    GlobalKt.dprintln("开始进化");
                    next(config);
                } else {
                    GlobalKt.dprintln("计算结束。");
                    controlThread.exit();
                    printBestFitness();
                }

            } else if (GlobalKt.getKEY_RESULT().equals(element.getOperation())) {
                GlobalKt.dprintln("一个计算进程计算适应度完成，开始存储数据");
                // TODO: 2018/6/11 存储数据
                List<List> subData = GlobalKt.getGson().fromJson(element.getData(), List.class);
                data.addAll(subData);   // 添加数据
                if (data.size() >= config.getSize()) {  // 所有个体适应度计算完毕
                    GlobalKt.dprintln("所有个体适应度计算完毕");
                    // 获取最佳适应度个体
                    updateBestFitness();
                    // 是否需要执行进化操作
                    if (hasNext(config)) {
                        // 执行进化操作
                        next(config);
                    } else {
                        GlobalKt.dprintln("计算完成。");
                        controlThread.exit();
                        printBestFitness();
                    }
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
    private static List<List> singleEvolve(List<List> data, int size, int chromosome_size,
                                           double crossover_probability, double mutation_probability) {
        List<List> result = (List<List>) ClojureKt.getEvolve().invoke(data, size, chromosome_size,
                crossover_probability, mutation_probability);
        GlobalKt.dprintln("进化后的数据为：\n" + result);
        return result;
    }

    /**
     * 是否进行下一代
     * 当前代数是否大于最大代数
     *
     * @return
     */
    private static boolean hasNext(Config config) {
        return currentGeneration <= config.getSize();
    }

    /**
     * 进行下一代计算适应度
     */
    private static void next(Config config) {
        currentGeneration++;
        nextGeneration.clear();
        nextGeneration.addAll(singleEvolve(data, config.getSize(), config.getChromosome_size(),
                config.getCrossover_probability(),
                config.getMutation_probability()));
        data.clear();
        data.addAll(nextGeneration);
        String json = GlobalKt.getGson().toJson(data);
        GlobalKt.dprintln("把数据发送到服务器，进行分布式运算");
        controlThread.send(json);
    }

    /**
     * 更新最佳适应度
     */
    private static void updateBestFitness() {
        for (List datum : data) {
            if (datum != null && datum.size() > 2) {
                double tempFitness = (double) datum.get(2);
                if (tempFitness > fitness) {
                    fitness = tempFitness;
                    x = (double) datum.get(0);
                    y = (double) datum.get(1);
                }
            }
        }
    }

    /**
     * 打印最佳适应度
     */
    private static void printBestFitness() {
        System.out.println();
        System.out.println("------最佳个体------");
        System.out.println("二进制编码为：\t(" + (int) x + "," + (int) y + ")");
        double realX = ClientLauncherKt.decode((int) x, config.getChromosome_size());
        double realY = ClientLauncherKt.decode((int) y, config.getChromosome_size());
        System.out.println("坐标为：\t(" + realX + "," + realY + ")");
        System.out.println("适应度：\t" + fitness);
    }

}