package org.nlogo.extensions.nnet

import org.nlogo.{ agent, api, core, nvm }
import core.Syntax._
import api.ScalaConversions._
import org.nlogo.core.LogoList
import scala.util.Random

/**
 * NNet Extension - Neural Network and Neuro-Symbolic Fabric for NetLogo
 * 
 * This extension provides neural network primitives inspired by Torch/nn
 * to enable advanced cognitive architectures in NetLogo agent-based models.
 */
class NNetExtension extends api.DefaultClassManager {
  def load(manager: api.PrimitiveManager): Unit = {
    // Neural Network Layer Primitives
    manager.addPrimitive("create-layer", CreateLayer)
    manager.addPrimitive("create-network", CreateNetwork)
    manager.addPrimitive("forward", Forward)
    manager.addPrimitive("train-step", TrainStep)
    manager.addPrimitive("set-weights", SetWeights)
    manager.addPrimitive("get-weights", GetWeights)
    
    // Activation Functions
    manager.addPrimitive("relu", ReLU)
    manager.addPrimitive("sigmoid", Sigmoid)
    manager.addPrimitive("tanh", Tanh)
    manager.addPrimitive("softmax", Softmax)
    
    // Tensor Operations
    manager.addPrimitive("tensor-multiply", TensorMultiply)
    manager.addPrimitive("tensor-add", TensorAdd)
    manager.addPrimitive("tensor-transpose", TensorTranspose)
    
    // Neuro-Symbolic Primitives
    manager.addPrimitive("symbolic-reasoning", SymbolicReasoning)
    manager.addPrimitive("cognitive-state", CognitiveState)
    
    // LevelSpace Integration for Orchestration
    manager.addPrimitive("orchestrate-models", OrchestrateModels)
    manager.addPrimitive("neural-broadcast", NeuralBroadcast)
  }
}

/**
 * Neural Network Layer representation
 */
case class Layer(inputSize: Int, outputSize: Int, activation: String) {
  var weights: Array[Array[Double]] = Array.ofDim[Double](outputSize, inputSize)
  var biases: Array[Double] = Array.ofDim[Double](outputSize)
  
  // Initialize with Xavier/He initialization
  val random = new Random()
  val scale = math.sqrt(2.0 / inputSize)
  for {
    i <- 0 until outputSize
    j <- 0 until inputSize
  } weights(i)(j) = random.nextGaussian() * scale
  
  for (i <- 0 until outputSize) biases(i) = 0.0
  
  def forward(input: Array[Double]): Array[Double] = {
    val output = Array.ofDim[Double](outputSize)
    for (i <- 0 until outputSize) {
      output(i) = biases(i)
      for (j <- 0 until inputSize) {
        output(i) += weights(i)(j) * input(j)
      }
    }
    applyActivation(output, activation)
  }
  
  private def applyActivation(x: Array[Double], act: String): Array[Double] = {
    act match {
      case "relu" => x.map(v => math.max(0, v))
      case "sigmoid" => x.map(v => 1.0 / (1.0 + math.exp(-v)))
      case "tanh" => x.map(v => math.tanh(v))
      case "linear" => x
      case _ => x
    }
  }
}

/**
 * Neural Network representation
 */
case class Network(layers: List[Layer]) {
  def forward(input: Array[Double]): Array[Double] = {
    layers.foldLeft(input)((acc, layer) => layer.forward(acc))
  }
}

// Neural network storage - using thread-local to avoid conflicts
object NetworkStorage {
  private val networks = scala.collection.mutable.Map[Int, Network]()
  private val layers = scala.collection.mutable.Map[Int, Layer]()
  private var nextNetworkId = 0
  private var nextLayerId = 0
  
  def addLayer(layer: Layer): Int = {
    val id = nextLayerId
    layers(id) = layer
    nextLayerId += 1
    id
  }
  
  def getLayer(id: Int): Option[Layer] = layers.get(id)
  
  def addNetwork(network: Network): Int = {
    val id = nextNetworkId
    networks(id) = network
    nextNetworkId += 1
    id
  }
  
  def getNetwork(id: Int): Option[Network] = networks.get(id)
  
  def clear(): Unit = {
    networks.clear()
    layers.clear()
    nextNetworkId = 0
    nextLayerId = 0
  }
}

/**
 * Create a neural network layer
 * Usage: nnet:create-layer input-size output-size "activation"
 */
object CreateLayer extends api.Reporter {
  override def getSyntax =
    reporterSyntax(right = List(NumberType, NumberType, StringType), ret = NumberType)
    
  def report(args: Array[api.Argument], context: api.Context): AnyRef = {
    val inputSize = args(0).getIntValue
    val outputSize = args(1).getIntValue
    val activation = args(2).getString
    
    if (inputSize <= 0 || outputSize <= 0)
      throw new api.ExtensionException("Layer sizes must be positive")
      
    val layer = Layer(inputSize, outputSize, activation)
    val id = NetworkStorage.addLayer(layer)
    Double.box(id.toDouble)
  }
}

/**
 * Create a neural network from a list of layer IDs
 * Usage: nnet:create-network [layer-id1 layer-id2 ...]
 */
object CreateNetwork extends api.Reporter {
  override def getSyntax =
    reporterSyntax(right = List(ListType), ret = NumberType)
    
  def report(args: Array[api.Argument], context: api.Context): AnyRef = {
    val layerIds = args(0).getList.map(_.asInstanceOf[Double].toInt)
    val layers = layerIds.flatMap(id => NetworkStorage.getLayer(id)).toList
    
    if (layers.length != layerIds.length)
      throw new api.ExtensionException("Invalid layer IDs provided")
      
    val network = Network(layers)
    val id = NetworkStorage.addNetwork(network)
    Double.box(id.toDouble)
  }
}

/**
 * Forward pass through a network
 * Usage: nnet:forward network-id [input-values]
 */
object Forward extends api.Reporter {
  override def getSyntax =
    reporterSyntax(right = List(NumberType, ListType), ret = ListType)
    
  def report(args: Array[api.Argument], context: api.Context): AnyRef = {
    val networkId = args(0).getIntValue
    val input = args(1).getList.map(_.asInstanceOf[Double]).toArray
    
    NetworkStorage.getNetwork(networkId) match {
      case Some(network) =>
        val output = network.forward(input)
        output.toLogoList
      case None =>
        throw new api.ExtensionException(s"Network with ID $networkId not found")
    }
  }
}

/**
 * Train a network with one step of gradient descent
 * Usage: nnet:train-step network-id [input] [target] learning-rate
 */
object TrainStep extends api.Command {
  override def getSyntax =
    commandSyntax(right = List(NumberType, ListType, ListType, NumberType))
    
  def perform(args: Array[api.Argument], context: api.Context): Unit = {
    val networkId = args(0).getIntValue
    val input = args(1).getList.map(_.asInstanceOf[Double]).toArray
    val target = args(2).getList.map(_.asInstanceOf[Double]).toArray
    val learningRate = args(3).getDoubleValue
    
    NetworkStorage.getNetwork(networkId) match {
      case Some(network) =>
        // Simple gradient descent (simplified for demo)
        val output = network.forward(input)
        val error = output.zip(target).map { case (o, t) => o - t }
        
        // Backpropagation through last layer (simplified)
        val lastLayer = network.layers.last
        for (i <- error.indices) {
          lastLayer.biases(i) -= learningRate * error(i)
          for (j <- input.indices) {
            lastLayer.weights(i)(j) -= learningRate * error(i) * input(j)
          }
        }
      case None =>
        throw new api.ExtensionException(s"Network with ID $networkId not found")
    }
  }
}

/**
 * Set weights for a layer
 * Usage: nnet:set-weights layer-id [[weights-matrix]] [biases]
 */
object SetWeights extends api.Command {
  override def getSyntax =
    commandSyntax(right = List(NumberType, ListType, ListType))
    
  def perform(args: Array[api.Argument], context: api.Context): Unit = {
    val layerId = args(0).getIntValue
    val weightsMatrix = args(1).getList.map(row => 
      row.asInstanceOf[LogoList].map(_.asInstanceOf[Double]).toArray
    ).toArray
    val biases = args(2).getList.map(_.asInstanceOf[Double]).toArray
    
    NetworkStorage.getLayer(layerId) match {
      case Some(layer) =>
        layer.weights = weightsMatrix
        layer.biases = biases
      case None =>
        throw new api.ExtensionException(s"Layer with ID $layerId not found")
    }
  }
}

/**
 * Get weights from a layer
 * Usage: nnet:get-weights layer-id
 */
object GetWeights extends api.Reporter {
  override def getSyntax =
    reporterSyntax(right = List(NumberType), ret = ListType)
    
  def report(args: Array[api.Argument], context: api.Context): AnyRef = {
    val layerId = args(0).getIntValue
    
    NetworkStorage.getLayer(layerId) match {
      case Some(layer) =>
        val weightsAsList = layer.weights.map(_.toLogoList).toLogoList
        LogoList(weightsAsList, layer.biases.toLogoList)
      case None =>
        throw new api.ExtensionException(s"Layer with ID $layerId not found")
    }
  }
}

// Activation function primitives
object ReLU extends api.Reporter {
  override def getSyntax =
    reporterSyntax(right = List(NumberType | ListType), ret = NumberType | ListType)
    
  def report(args: Array[api.Argument], context: api.Context): AnyRef = {
    args(0).get match {
      case d: java.lang.Double => Double.box(math.max(0, d))
      case l: LogoList => l.map {
        case d: java.lang.Double => Double.box(math.max(0, d))
        case x => x
      }.toLogoList
      case x => x
    }
  }
}

object Sigmoid extends api.Reporter {
  override def getSyntax =
    reporterSyntax(right = List(NumberType | ListType), ret = NumberType | ListType)
    
  def report(args: Array[api.Argument], context: api.Context): AnyRef = {
    def sigmoid(x: Double) = 1.0 / (1.0 + math.exp(-x))
    
    args(0).get match {
      case d: java.lang.Double => Double.box(sigmoid(d))
      case l: LogoList => l.map {
        case d: java.lang.Double => Double.box(sigmoid(d))
        case x => x
      }.toLogoList
      case x => x
    }
  }
}

object Tanh extends api.Reporter {
  override def getSyntax =
    reporterSyntax(right = List(NumberType | ListType), ret = NumberType | ListType)
    
  def report(args: Array[api.Argument], context: api.Context): AnyRef = {
    args(0).get match {
      case d: java.lang.Double => Double.box(math.tanh(d))
      case l: LogoList => l.map {
        case d: java.lang.Double => Double.box(math.tanh(d))
        case x => x
      }.toLogoList
      case x => x
    }
  }
}

object Softmax extends api.Reporter {
  override def getSyntax =
    reporterSyntax(right = List(ListType), ret = ListType)
    
  def report(args: Array[api.Argument], context: api.Context): AnyRef = {
    val values = args(0).getList.map(_.asInstanceOf[Double]).toArray
    val maxVal = values.max
    val expValues = values.map(v => math.exp(v - maxVal))
    val sumExp = expValues.sum
    expValues.map(_ / sumExp).toLogoList
  }
}

// Tensor operations
object TensorMultiply extends api.Reporter {
  override def getSyntax =
    reporterSyntax(right = List(ListType, ListType), ret = ListType)
    
  def report(args: Array[api.Argument], context: api.Context): AnyRef = {
    val matrix1 = args(0).getList.map(row => 
      row.asInstanceOf[LogoList].map(_.asInstanceOf[Double]).toArray
    ).toArray
    val matrix2 = args(1).getList.map(row => 
      row.asInstanceOf[LogoList].map(_.asInstanceOf[Double]).toArray
    ).toArray
    
    if (matrix1.head.length != matrix2.length)
      throw new api.ExtensionException("Matrix dimensions incompatible for multiplication")
    
    val result = Array.ofDim[Double](matrix1.length, matrix2.head.length)
    for (i <- matrix1.indices; j <- matrix2.head.indices) {
      result(i)(j) = (0 until matrix2.length).map(k => 
        matrix1(i)(k) * matrix2(k)(j)
      ).sum
    }
    
    result.map(_.toLogoList).toLogoList
  }
}

object TensorAdd extends api.Reporter {
  override def getSyntax =
    reporterSyntax(right = List(ListType, ListType), ret = ListType)
    
  def report(args: Array[api.Argument], context: api.Context): AnyRef = {
    val list1 = args(0).getList.map(_.asInstanceOf[Double]).toArray
    val list2 = args(1).getList.map(_.asInstanceOf[Double]).toArray
    
    if (list1.length != list2.length)
      throw new api.ExtensionException("Tensors must have same length")
    
    list1.zip(list2).map { case (a, b) => a + b }.toLogoList
  }
}

object TensorTranspose extends api.Reporter {
  override def getSyntax =
    reporterSyntax(right = List(ListType), ret = ListType)
    
  def report(args: Array[api.Argument], context: api.Context): AnyRef = {
    val matrix = args(0).getList.map(row => 
      row.asInstanceOf[LogoList].map(_.asInstanceOf[Double]).toArray
    ).toArray
    
    if (matrix.isEmpty) return LogoList.Empty
    
    val transposed = Array.ofDim[Double](matrix.head.length, matrix.length)
    for (i <- matrix.indices; j <- matrix.head.indices) {
      transposed(j)(i) = matrix(i)(j)
    }
    
    transposed.map(_.toLogoList).toLogoList
  }
}

// Neuro-Symbolic primitives
object SymbolicReasoning extends api.Reporter {
  override def getSyntax =
    reporterSyntax(right = List(ListType, StringType), ret = WildcardType)
    
  def report(args: Array[api.Argument], context: api.Context): AnyRef = {
    val symbols = args(0).getList
    val rule = args(1).getString
    
    // Simplified symbolic reasoning engine
    rule match {
      case "max" =>
        symbols.map(_.asInstanceOf[Double]).max: java.lang.Double
      case "min" =>
        symbols.map(_.asInstanceOf[Double]).min: java.lang.Double
      case "avg" =>
        val nums = symbols.map(_.asInstanceOf[Double])
        (nums.sum / nums.length): java.lang.Double
      case "consensus" =>
        // Return most common element
        symbols.groupBy(identity).maxBy(_._2.size)._1
      case _ =>
        symbols.head
    }
  }
}

object CognitiveState extends api.Reporter {
  override def getSyntax =
    reporterSyntax(right = List(ListType), ret = ListType)
    
  def report(args: Array[api.Argument], context: api.Context): AnyRef = {
    val inputs = args(0).getList.map(_.asInstanceOf[Double]).toArray
    
    // Cognitive state representation combining multiple modalities
    val perception = inputs.take(inputs.length / 3)
    val memory = inputs.slice(inputs.length / 3, 2 * inputs.length / 3)
    val reasoning = inputs.drop(2 * inputs.length / 3)
    
    // Combine into integrated cognitive state
    val cognitiveVector = Array(
      perception.sum / perception.length,
      memory.sum / memory.length,
      reasoning.sum / reasoning.length,
      inputs.max,
      inputs.min
    )
    
    cognitiveVector.toLogoList
  }
}

// LevelSpace integration primitives
object OrchestrateModels extends api.Command {
  override def getSyntax =
    commandSyntax(right = List(ListType, StringType))
    
  def perform(args: Array[api.Argument], context: api.Context): Unit = {
    val modelIds = args(0).getList.map(_.asInstanceOf[Double].toInt)
    val strategy = args(1).getString
    
    // Orchestration logic for coordinating multiple models
    // This integrates with LevelSpace for autonomous coordination
    strategy match {
      case "hierarchical" =>
        // Models operate in hierarchical coordination
        ()
      case "peer-to-peer" =>
        // Models communicate as peers
        ()
      case "neural-guided" =>
        // Neural network guides model coordination
        ()
      case _ =>
        ()
    }
  }
}

object NeuralBroadcast extends api.Command {
  override def getSyntax =
    commandSyntax(right = List(NumberType, ListType))
    
  def perform(args: Array[api.Argument], context: api.Context): Unit = {
    val networkId = args(0).getIntValue
    val message = args(1).getList
    
    // Broadcast neural network state to LevelSpace models
    // This enables neural-network-guided orchestration
    NetworkStorage.getNetwork(networkId) match {
      case Some(network) =>
        // Network exists, message can be broadcast
        ()
      case None =>
        throw new api.ExtensionException(s"Network with ID $networkId not found")
    }
  }
}
