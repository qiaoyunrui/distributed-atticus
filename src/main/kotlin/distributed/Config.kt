package distributed

data class Config(var function: String = "",    // 计算函数
                  var chromosome_size: Int = 25,    // 染色体长度
                  var size: Int = 50,   // 种群规模，即个体数量
                  var max_generation: Int = 50,  //最大代数
                  var crossover_probability: Double = 0.8,    //交叉概率
                  var mutation_probability: Double = 0.5)  //变异概率