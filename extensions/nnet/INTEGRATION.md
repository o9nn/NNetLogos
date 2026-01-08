# NNet Extension Integration Guide

## Overview

The NNet (Neural Network) extension integrates Torch-like neural network capabilities with NetLogo's agent-based modeling framework, inspired by PyTorch's `torch.nn` module and Torch7. This extension enables:

1. **Advanced Cognitive Architectures** - Build neural-network-powered agents with perception, reasoning, and decision-making capabilities
2. **Neuro-Symbolic Computing** - Combine neural networks with symbolic reasoning for hybrid AI systems
3. **LevelSpace Orchestration** - Use neural networks to autonomously coordinate multiple NetLogo models

## Architecture

### Core Components

The NNet extension consists of several integrated components:

```
NNet Extension
├── Neural Network Engine
│   ├── Layer Management
│   ├── Forward Propagation
│   ├── Training (Gradient Descent)
│   └── Weight Management
├── Activation Functions
│   ├── ReLU, Sigmoid, Tanh
│   └── Softmax
├── Tensor Operations
│   ├── Matrix Multiplication
│   ├── Addition
│   └── Transposition
├── Neuro-Symbolic Integration
│   ├── Symbolic Reasoning
│   └── Cognitive State Representation
└── LevelSpace Integration
    ├── Model Orchestration
    └── Neural Broadcasting
```

### Integration with Torch Concepts

The extension maps Torch/PyTorch concepts to NetLogo:

| Torch/PyTorch | NNet Extension | NetLogo Use Case |
|---------------|----------------|------------------|
| `nn.Linear` | `create-layer` | Agent neural networks |
| `nn.Sequential` | `create-network` | Multi-layer networks |
| `forward()` | `forward` | Process agent perceptions |
| `backward()` | `train-step` | Agent learning |
| `torch.relu` | `relu` | Activation functions |
| `torch.matmul` | `tensor-multiply` | State transformations |

## Building and Installation

### Prerequisites

- Java 11 or later
- SBT (Scala Build Tool) 1.10.1
- NetLogo 7.0.0 or later

### Building from Source

```bash
cd extensions/nnet
sbt package
```

This creates `nnet.jar` in `extensions/nnet/target/`.

### Installation

1. Copy the built JAR to NetLogo's extensions directory
2. Or keep in the extension subdirectory structure
3. NetLogo will automatically find it when you use `extensions [nnet]`

## Usage Patterns

### Pattern 1: Agent Cognitive Architecture

Each agent has its own neural network for decision-making:

```netlogo
extensions [nnet]

turtles-own [
  my-network
  cognitive-state
]

to setup
  create-turtles 100 [
    ; Each turtle gets its own neural network
    let layer1 nnet:create-layer 10 20 "relu"
    let layer2 nnet:create-layer 20 5 "sigmoid"
    set my-network nnet:create-network (list layer1 layer2)
  ]
end

to go
  ask turtles [
    ; Perceive environment
    let inputs get-sensory-inputs
    
    ; Process through neural network
    let decision nnet:forward my-network inputs
    
    ; Act on decision
    execute-action decision
  ]
end
```

### Pattern 2: Shared Neural Network

All agents share a global neural network (like a learned policy):

```netlogo
extensions [nnet]

globals [shared-policy-network]

to setup
  ; Create a shared policy network
  let p1 nnet:create-layer 8 16 "relu"
  let p2 nnet:create-layer 16 4 "softmax"
  set shared-policy-network nnet:create-network (list p1 p2)
  
  create-turtles 100
end

to go
  ask turtles [
    let state get-state
    let action-probs nnet:forward shared-policy-network state
    let action select-action action-probs
    execute-action action
  ]
  
  ; Train the shared network periodically
  if ticks mod 10 = 0 [
    train-shared-network
  ]
end
```

### Pattern 3: Hierarchical Neural Systems

Multiple networks for different cognitive functions:

```netlogo
extensions [nnet]

globals [
  perception-net
  memory-net
  decision-net
]

to setup
  ; Perception network - processes raw sensory input
  set perception-net create-perception-network
  
  ; Memory network - maintains temporal state
  set memory-net create-memory-network
  
  ; Decision network - makes final decisions
  set decision-net create-decision-network
end

to go
  ask turtles [
    ; Multi-stage processing
    let raw-input get-sensors
    let perceived nnet:forward perception-net raw-input
    let remembered nnet:forward memory-net perceived
    let decision nnet:forward decision-net remembered
    
    execute-decision decision
  ]
end
```

### Pattern 4: LevelSpace Orchestration

Neural networks coordinate multiple models:

```netlogo
extensions [nnet ls]

globals [
  orchestrator-network
  child-models
]

to setup
  ; Create orchestrator
  let o1 nnet:create-layer 10 20 "relu"
  let o2 nnet:create-layer 20 5 "tanh"
  set orchestrator-network nnet:create-network (list o1 o2)
  
  ; Load child models
  set child-models ls:create-models 5 "agent-model.nlogo"
  ls:ask ls:models [ setup ]
end

to go
  ; Collect states from all models
  let states ls:report ls:models [ get-aggregate-state ]
  
  ; Neural orchestration decision
  let coordination-signal nnet:forward orchestrator-network (reduce sentence states)
  
  ; Broadcast to models
  nnet:neural-broadcast orchestrator-network coordination-signal
  
  ; Coordinate execution
  nnet:orchestrate-models child-models "neural-guided"
  
  ; Run models
  ls:ask ls:models [ go ]
end
```

## Neuro-Symbolic Integration

The extension enables hybrid systems combining neural and symbolic approaches:

### Symbolic Grounding

```netlogo
; Neural perception with symbolic interpretation
let perception nnet:forward perception-net inputs
let symbolic-state nnet:symbolic-reasoning perception "consensus"

; Act based on symbolic interpretation
if symbolic-state > threshold [
  ; symbolic rule: high activation = threat
  avoid-threat
]
```

### Cognitive State Integration

```netlogo
; Integrate multiple modalities into cognitive state
let visual-input get-visual-input
let audio-input get-audio-input
let internal-state get-internal-state

let all-inputs sentence visual-input (sentence audio-input internal-state)
let cognitive-state nnet:cognitive-state all-inputs

; Cognitive state now represents:
; [perception-level memory-level reasoning-level max-activation min-activation]
```

## LevelSpace Integration Details

### Autonomous Orchestration Strategies

The extension supports three orchestration strategies:

1. **Hierarchical**: Parent model controls child models in a tree structure
   ```netlogo
   nnet:orchestrate-models child-ids "hierarchical"
   ```

2. **Peer-to-Peer**: Models coordinate as equals
   ```netlogo
   nnet:orchestrate-models child-ids "peer-to-peer"
   ```

3. **Neural-Guided**: Neural network determines coordination dynamically
   ```netlogo
   nnet:orchestrate-models child-ids "neural-guided"
   ```

### Neural Broadcasting

Share neural network state with child models:

```netlogo
; Parent model
let network-state nnet:forward orchestrator-network inputs
nnet:neural-broadcast orchestrator-network network-state

; Child models receive and can process the signal
; (in child model code)
to receive-neural-signal [signal]
  ; Process coordination signal
  let my-response process-signal signal
  ; Adjust behavior accordingly
end
```

## Performance Considerations

### Network Size

- Small networks (< 100 neurons total): Fast, suitable for many agents
- Medium networks (100-1000 neurons): Moderate speed, use for fewer agents
- Large networks (> 1000 neurons): Slower, use sparingly or for global policies

### Optimization Tips

1. **Reuse layers**: Don't create duplicate layers
   ```netlogo
   ; Good: Create once
   let shared-layer nnet:create-layer 10 20 "relu"
   
   ; Bad: Creating in loop
   ask turtles [
     let layer nnet:create-layer 10 20 "relu"  ; Wasteful!
   ]
   ```

2. **Batch operations**: Process multiple agents together when possible
   ```netlogo
   ; Collect all inputs
   let all-inputs [get-inputs] of turtles
   
   ; Process in parallel (conceptually)
   let all-outputs map [inputs -> nnet:forward network inputs] all-inputs
   ```

3. **Cache results**: Store network outputs that don't change frequently
   ```netlogo
   turtles-own [cached-output cache-age]
   
   ask turtles [
     if cache-age > 10 [
       set cached-output nnet:forward network inputs
       set cache-age 0
     ]
     set cache-age cache-age + 1
   ]
   ```

## Advanced Topics

### Custom Activation Functions

While the extension provides standard activations, you can create custom ones:

```netlogo
to-report custom-activation [values]
  ; Apply custom transformation
  report map [v -> 
    ; Example: Leaky ReLU
    ifelse-value (v > 0) [v] [0.01 * v]
  ] values
end

; Use with linear layer
let layer nnet:create-layer 10 5 "linear"
let raw-output nnet:forward network inputs
let activated custom-activation raw-output
```

### Training Strategies

The extension provides basic gradient descent. For advanced training:

```netlogo
; Collect training examples
globals [training-buffer]

to collect-experience [input target]
  set training-buffer lput (list input target) training-buffer
  if length training-buffer > 100 [
    set training-buffer but-first training-buffer
  ]
end

to train-network
  ; Mini-batch training
  let batch-size 10
  let batch n-of (min batch-size length training-buffer) training-buffer
  
  foreach batch [ example ->
    let inputs item 0 example
    let target item 1 example
    nnet:train-step network inputs target 0.01
  ]
end
```

### Saving and Loading Networks

Currently, networks exist only in memory. To persist:

```netlogo
; Save weights to file
to save-network [network-id filename]
  let layers get-network-layers network-id
  foreach layers [ layer-id ->
    let weights nnet:get-weights layer-id
    ; Write to file using file primitives
    file-open filename
    file-print weights
    file-close
  ]
end

; Load weights from file
to load-network [network-id filename]
  ; Read from file and restore weights
  file-open filename
  ; ... read and apply weights
  file-close
end
```

## Troubleshooting

### Common Issues

1. **"Layer/Network ID not found"**
   - Store layer/network IDs in globals or agent variables
   - Don't recreate IDs that should persist

2. **"Matrix dimensions incompatible"**
   - Check input size matches first layer input size
   - Check intermediate layer connections are valid

3. **"Out of memory"**
   - Reduce network size
   - Reduce number of agents with individual networks
   - Use shared networks when possible

## Examples Repository

Complete examples are in `extensions/nnet/example-models/`:

- `simple-neural-network.nlogo` - Basic neural network usage
- `cognitive-architecture.nlogo` - Agent cognitive systems
- `levelspace-orchestration.nlogo` - Multi-model coordination

## API Reference

See `README.md` for complete primitive reference.

## Contributing

To contribute to the NNet extension:

1. Fork the NetLogo repository
2. Make changes in `extensions/nnet/`
3. Add tests to `extensions/nnet/tests.txt`
4. Submit a pull request

## License

GPL-2.0, same as NetLogo core.

## Support

- NetLogo Forum: https://forum.netlogo.org
- GitHub Issues: https://github.com/NetLogo/NetLogo/issues
- Email: bugs@ccl.northwestern.edu

## References

- PyTorch Neural Networks: https://pytorch.org/docs/stable/nn.html
- Torch7: https://github.com/torch/nn
- LevelSpace: https://github.com/NetLogo/LevelSpace
- NetLogo Extensions API: https://github.com/NetLogo/NetLogo/wiki/Extensions-API
