# NNet Extension for NetLogo

The NNet extension provides neural network and neuro-symbolic reasoning capabilities for NetLogo, inspired by PyTorch's neural network module (torch.nn) and Torch7. It enables advanced cognitive architectures and integrates with LevelSpace for autonomous orchestration of agent-based models.

## Installation

1. Place the `nnet` folder in your NetLogo extensions directory
2. Add `extensions [nnet]` at the top of your NetLogo model
3. Build the extension with `sbt package` (for developers)

## Features

### Neural Network Primitives

The NNet extension provides a complete neural network framework:

- **Layer Creation**: Build neural network layers with configurable sizes and activation functions
- **Network Construction**: Combine layers into deep neural networks
- **Forward Propagation**: Process inputs through networks
- **Training**: Simple gradient descent training
- **Weight Management**: Set and retrieve network weights

### Activation Functions

Supported activation functions:
- `nnet:relu` - Rectified Linear Unit
- `nnet:sigmoid` - Sigmoid activation
- `nnet:tanh` - Hyperbolic tangent
- `nnet:softmax` - Softmax for probability distributions

### Tensor Operations

Matrix and tensor operations for neural computations:
- `nnet:tensor-multiply` - Matrix multiplication
- `nnet:tensor-add` - Element-wise addition
- `nnet:tensor-transpose` - Matrix transposition

### Neuro-Symbolic Integration

Combine neural networks with symbolic reasoning:
- `nnet:symbolic-reasoning` - Apply symbolic rules (max, min, avg, consensus)
- `nnet:cognitive-state` - Generate integrated cognitive state representations

### LevelSpace Integration

Orchestrate multiple NetLogo models with neural guidance:
- `nnet:orchestrate-models` - Coordinate multiple models with different strategies
- `nnet:neural-broadcast` - Broadcast neural network states to child models

## Primitive Reference

### nnet:create-layer

```netlogo
nnet:create-layer input-size output-size activation-function
```

Creates a neural network layer and returns its ID.

**Parameters:**
- `input-size` (number): Number of input neurons
- `output-size` (number): Number of output neurons  
- `activation-function` (string): Activation function ("relu", "sigmoid", "tanh", or "linear")

**Returns:** Layer ID (number)

**Example:**
```netlogo
let layer-id nnet:create-layer 10 5 "relu"
```

### nnet:create-network

```netlogo
nnet:create-network [layer-id1 layer-id2 ...]
```

Creates a neural network from a list of layer IDs.

**Parameters:**
- List of layer IDs

**Returns:** Network ID (number)

**Example:**
```netlogo
let layer1 nnet:create-layer 10 20 "relu"
let layer2 nnet:create-layer 20 10 "relu"
let layer3 nnet:create-layer 10 2 "sigmoid"
let network nnet:create-network (list layer1 layer2 layer3)
```

### nnet:forward

```netlogo
nnet:forward network-id input-list
```

Performs forward propagation through a network.

**Parameters:**
- `network-id` (number): Network ID
- `input-list` (list): Input values

**Returns:** Output list

**Example:**
```netlogo
let output nnet:forward network [0.5 0.3 0.8 0.1 0.9 0.2 0.7 0.4 0.6 0.5]
```

### nnet:train-step

```netlogo
nnet:train-step network-id input-list target-list learning-rate
```

Performs one step of gradient descent training.

**Parameters:**
- `network-id` (number): Network ID
- `input-list` (list): Input values
- `target-list` (list): Target/expected output values
- `learning-rate` (number): Learning rate for gradient descent

**Example:**
```netlogo
nnet:train-step network [0.5 0.3] [1.0 0.0] 0.01
```

### nnet:set-weights

```netlogo
nnet:set-weights layer-id weights-matrix biases-list
```

Sets the weights and biases for a layer.

**Parameters:**
- `layer-id` (number): Layer ID
- `weights-matrix` (list of lists): Weight matrix
- `biases-list` (list): Bias values

**Example:**
```netlogo
nnet:set-weights layer1 [[0.5 0.3] [0.8 0.2]] [0.1 0.1]
```

### nnet:get-weights

```netlogo
nnet:get-weights layer-id
```

Retrieves the weights and biases from a layer.

**Parameters:**
- `layer-id` (number): Layer ID

**Returns:** List containing [weights-matrix biases-list]

**Example:**
```netlogo
let weights-and-biases nnet:get-weights layer1
```

### nnet:relu

```netlogo
nnet:relu value-or-list
```

Applies ReLU activation function: max(0, x)

**Parameters:**
- `value-or-list` (number or list): Input value(s)

**Returns:** Activated value(s)

**Example:**
```netlogo
show nnet:relu -5  ; 0
show nnet:relu 3   ; 3
show nnet:relu [-2 0 3]  ; [0 0 3]
```

### nnet:sigmoid

```netlogo
nnet:sigmoid value-or-list
```

Applies sigmoid activation: 1 / (1 + e^(-x))

**Parameters:**
- `value-or-list` (number or list): Input value(s)

**Returns:** Activated value(s)

**Example:**
```netlogo
show nnet:sigmoid 0  ; 0.5
show nnet:sigmoid [0 1 -1]
```

### nnet:tanh

```netlogo
nnet:tanh value-or-list
```

Applies hyperbolic tangent activation.

**Parameters:**
- `value-or-list` (number or list): Input value(s)

**Returns:** Activated value(s)

**Example:**
```netlogo
show nnet:tanh 0  ; 0
```

### nnet:softmax

```netlogo
nnet:softmax list
```

Applies softmax function to convert to probability distribution.

**Parameters:**
- `list` (list): Input values

**Returns:** Probability distribution (list summing to 1.0)

**Example:**
```netlogo
show nnet:softmax [1 2 3]  ; [0.09 0.24 0.67] (approximately)
```

### nnet:tensor-multiply

```netlogo
nnet:tensor-multiply matrix1 matrix2
```

Performs matrix multiplication.

**Parameters:**
- `matrix1` (list of lists): First matrix
- `matrix2` (list of lists): Second matrix

**Returns:** Result matrix

**Example:**
```netlogo
let result nnet:tensor-multiply [[1 2] [3 4]] [[5 6] [7 8]]
```

### nnet:tensor-add

```netlogo
nnet:tensor-add list1 list2
```

Element-wise addition of two lists.

**Parameters:**
- `list1` (list): First list
- `list2` (list): Second list

**Returns:** Sum list

**Example:**
```netlogo
show nnet:tensor-add [1 2 3] [4 5 6]  ; [5 7 9]
```

### nnet:tensor-transpose

```netlogo
nnet:tensor-transpose matrix
```

Transposes a matrix.

**Parameters:**
- `matrix` (list of lists): Input matrix

**Returns:** Transposed matrix

**Example:**
```netlogo
show nnet:tensor-transpose [[1 2 3] [4 5 6]]  ; [[1 4] [2 5] [3 6]]
```

### nnet:symbolic-reasoning

```netlogo
nnet:symbolic-reasoning symbol-list rule
```

Applies symbolic reasoning rules to data.

**Parameters:**
- `symbol-list` (list): Input symbols/values
- `rule` (string): Rule to apply ("max", "min", "avg", "consensus")

**Returns:** Result based on rule

**Example:**
```netlogo
show nnet:symbolic-reasoning [1 5 3 9 2] "max"  ; 9
show nnet:symbolic-reasoning [1 5 3 9 2] "avg"  ; 4.0
```

### nnet:cognitive-state

```netlogo
nnet:cognitive-state input-list
```

Generates an integrated cognitive state representation from inputs.

**Parameters:**
- `input-list` (list): Input values representing different cognitive modalities

**Returns:** Cognitive state vector

**Example:**
```netlogo
let state nnet:cognitive-state [0.1 0.2 0.3 0.4 0.5 0.6]
; Returns integrated representation: [perception memory reasoning max min]
```

### nnet:orchestrate-models

```netlogo
nnet:orchestrate-models model-id-list strategy
```

Orchestrates multiple LevelSpace models using specified strategy.

**Parameters:**
- `model-id-list` (list): List of LevelSpace model IDs
- `strategy` (string): Orchestration strategy ("hierarchical", "peer-to-peer", "neural-guided")

**Example:**
```netlogo
extensions [nnet ls]
; After loading models with ls:
nnet:orchestrate-models [0 1 2] "neural-guided"
```

### nnet:neural-broadcast

```netlogo
nnet:neural-broadcast network-id message-list
```

Broadcasts neural network state to LevelSpace models.

**Parameters:**
- `network-id` (number): Neural network ID
- `message-list` (list): Message to broadcast

**Example:**
```netlogo
nnet:neural-broadcast network [0.5 0.8 0.3]
```

## Usage Examples

### Simple Neural Network

```netlogo
extensions [nnet]

to setup
  clear-all
  
  ; Create a simple 3-layer network
  let layer1 nnet:create-layer 2 4 "relu"
  let layer2 nnet:create-layer 4 3 "relu"
  let layer3 nnet:create-layer 3 1 "sigmoid"
  
  let my-network nnet:create-network (list layer1 layer2 layer3)
  
  ; Forward pass
  let output nnet:forward my-network [0.5 0.8]
  print (word "Output: " output)
  
  ; Train for one step
  nnet:train-step my-network [0.5 0.8] [1.0] 0.01
end
```

### Cognitive Architecture

```netlogo
extensions [nnet]

globals [perception-net reasoning-net cognitive-state]

to setup
  clear-all
  
  ; Perception network
  let p1 nnet:create-layer 10 20 "relu"
  let p2 nnet:create-layer 20 10 "tanh"
  set perception-net nnet:create-network (list p1 p2)
  
  ; Reasoning network
  let r1 nnet:create-layer 10 15 "relu"
  let r2 nnet:create-layer 15 5 "softmax"
  set reasoning-net nnet:create-network (list r1 r2)
  
  reset-ticks
end

to go
  ask turtles [
    ; Get sensory input
    let sensory-input get-sensory-input
    
    ; Process through perception network
    let perception nnet:forward perception-net sensory-input
    
    ; Generate cognitive state
    set cognitive-state nnet:cognitive-state perception
    
    ; Make decision through reasoning network
    let decision nnet:forward reasoning-net cognitive-state
    
    ; Apply symbolic reasoning
    let action nnet:symbolic-reasoning decision "max"
    
    ; Act based on decision
    execute-action action
  ]
  
  tick
end

to-report get-sensory-input
  ; Collect sensory information
  report n-values 10 [random-float 1.0]
end

to execute-action [action]
  ; Execute the chosen action
  forward action
  rt random 30 - 15
end
```

### LevelSpace Orchestration

```netlogo
extensions [nnet ls]

globals [orchestrator-network model-ids]

to setup
  clear-all
  ls:reset
  
  ; Create orchestrator network
  let o1 nnet:create-layer 5 10 "relu"
  let o2 nnet:create-layer 10 5 "tanh"
  set orchestrator-network nnet:create-network (list o1 o2)
  
  ; Load child models
  set model-ids []
  repeat 3 [
    let model-id ls:create-models 1 "simple-model.nlogo"
    set model-ids lput model-id model-ids
  ]
  
  ; Setup models
  ls:ask ls:models [ setup ]
  
  reset-ticks
end

to go
  ; Collect state from all models
  let states ls:report ls:models [ get-state ]
  
  ; Process through neural network
  let orchestration-signal nnet:forward orchestrator-network (reduce sentence states)
  
  ; Broadcast to models
  nnet:neural-broadcast orchestrator-network orchestration-signal
  
  ; Orchestrate models
  nnet:orchestrate-models model-ids "neural-guided"
  
  ; Run models
  ls:ask ls:models [ go ]
  
  tick
end
```

## Integration with LevelSpace

The NNet extension is designed to work seamlessly with LevelSpace for:

1. **Neural-Guided Orchestration**: Use neural networks to coordinate multiple models
2. **Distributed Cognitive Architectures**: Spread cognitive processing across models
3. **Hierarchical Learning**: Parent models learn from child model behaviors
4. **Emergent Coordination**: Models develop coordination through neural signals

## Advanced Cognitive Architectures

The extension supports building:

- **Perception-Action Loops**: Sensor → Neural Processing → Action
- **Memory Systems**: Recurrent patterns for temporal information
- **Decision Making**: Multi-layer reasoning with symbolic grounding
- **Learning Agents**: Agents that adapt behavior through training
- **Multi-Agent Coordination**: Neural-network-guided swarm behaviors

## Implementation Notes

- Neural networks are stored globally and identified by integer IDs
- Weights are initialized using He/Xavier initialization
- Training uses simplified gradient descent (full backpropagation in development)
- All tensor operations work with NetLogo lists
- Compatible with LevelSpace for model orchestration

## Performance Considerations

- For large networks, forward passes may take time proportional to network size
- Training is simplified; for complex tasks, consider external training
- Tensor operations are pure Scala implementations (no native acceleration)
- Store network IDs in global variables or agent attributes

## Future Enhancements

Planned features:
- Full backpropagation algorithm
- Convolutional and recurrent layer types
- Model saving/loading from files
- Integration with external ML frameworks
- Distributed training across LevelSpace models
- Advanced optimizers (Adam, RMSprop)

## License

This extension is part of NetLogo and follows the same GPL-2.0 license.

## Citation

If you use NNet in your research, please cite:

```
NNet Extension for NetLogo
Neural Network and Neuro-Symbolic Fabric
https://github.com/NetLogo/NetLogo
```

## Support

For questions and issues:
- NetLogo Forum: https://forum.netlogo.org
- GitHub Issues: https://github.com/NetLogo/NetLogo/issues
- Mailing List: netlogo-devel@googlegroups.com
