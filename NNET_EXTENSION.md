# NNetLogos: Neural Network Extension for NetLogo

## Overview

**NNetLogos** extends NetLogo with neural network and neuro-symbolic computing capabilities inspired by [Torch](https://github.com/torch/torch7) and [PyTorch's nn module](https://github.com/torch/nn). This extension enables:

- 🧠 **Advanced Cognitive Architectures** for agent-based models
- 🔀 **Neuro-Symbolic Fabric** combining learning and reasoning
- 🌐 **LevelSpace Orchestration** for autonomous multi-model coordination
- 🚀 **Modular Deployment** of agent constellations for spatial computing

## Key Features

### Neural Network Primitives (Torch-inspired)

```netlogo
extensions [nnet]

; Create layers (like torch.nn.Linear)
let layer1 nnet:create-layer 10 20 "relu"
let layer2 nnet:create-layer 20 5 "sigmoid"

; Build network (like torch.nn.Sequential)
let network nnet:create-network (list layer1 layer2)

; Forward propagation
let output nnet:forward network [0.5 0.8 0.3 ...]

; Training (gradient descent)
nnet:train-step network [inputs] [targets] 0.01
```

### Activation Functions

- `nnet:relu` - Rectified Linear Unit
- `nnet:sigmoid` - Sigmoid activation
- `nnet:tanh` - Hyperbolic tangent
- `nnet:softmax` - Softmax for probabilities

### Tensor Operations

```netlogo
; Matrix multiplication
let result nnet:tensor-multiply matrix1 matrix2

; Element-wise addition
let sum nnet:tensor-add vector1 vector2

; Matrix transpose
let transposed nnet:tensor-transpose matrix
```

### Neuro-Symbolic Integration

```netlogo
; Symbolic reasoning over neural outputs
let neural-output nnet:forward network inputs
let decision nnet:symbolic-reasoning neural-output "max"

; Cognitive state representation
let integrated-state nnet:cognitive-state multi-modal-inputs
```

### LevelSpace Orchestration

```netlogo
extensions [nnet ls]

; Neural-guided orchestration
nnet:orchestrate-models model-ids "neural-guided"

; Broadcast neural states to child models
nnet:neural-broadcast network-id coordination-signal
```

## Quick Start

### Installation

1. Build the extension:
   ```bash
   cd extensions/nnet
   sbt package
   ```

2. Use in your NetLogo model:
   ```netlogo
   extensions [nnet]
   
   to setup
     ; Your code with neural networks
   end
   ```

### Simple Example

```netlogo
extensions [nnet]

globals [my-network]

to setup
  clear-all
  
  ; Create 3-layer network
  let l1 nnet:create-layer 2 4 "relu"
  let l2 nnet:create-layer 4 3 "relu"
  let l3 nnet:create-layer 3 1 "sigmoid"
  set my-network nnet:create-network (list l1 l2 l3)
  
  create-turtles 50
  reset-ticks
end

to go
  ask turtles [
    ; Get inputs from environment
    let inputs (list (xcor / max-pxcor) (ycor / max-pycor))
    
    ; Process through neural network
    let output nnet:forward my-network inputs
    
    ; Act on output
    set color scale-color red (item 0 output) 0 1
    rt random 30 - 15
    fd 0.5
  ]
  tick
end
```

## Documentation

- **[README.md](extensions/nnet/README.md)** - Complete primitive reference
- **[INTEGRATION.md](extensions/nnet/INTEGRATION.md)** - Integration guide and usage patterns
- **[ARCHITECTURE.md](extensions/nnet/ARCHITECTURE.md)** - Neuro-symbolic fabric architecture

## Example Models

Located in `extensions/nnet/example-models/`:

1. **simple-neural-network.nlogo** - Basic neural network usage
2. **cognitive-architecture.nlogo** - Agent cognitive systems with perception-reasoning-action
3. **levelspace-orchestration.nlogo** - Multi-model autonomous coordination

## Architecture

### Neuro-Symbolic Fabric

The extension implements a **neuro-symbolic fabric** that weaves together:

```
┌─────────────────────────────────────────┐
│        Symbolic Layer                   │
│  • Logic-based reasoning                │
│  • Explainable decisions                │
│  • Rule application                     │
└──────────────┬──────────────────────────┘
               │ Integration
┌──────────────▼──────────────────────────┐
│      Neural Layer (Torch-inspired)      │
│  • Pattern recognition                  │
│  • Learning from experience             │
│  • Continuous representations           │
└─────────────────────────────────────────┘
```

### LevelSpace Integration

```
          ┌─────────────────────────┐
          │  Meta-Cognitive Layer   │
          │  (Neural Orchestrator)  │
          └───────────┬─────────────┘
                      │
      ┌───────────────┼───────────────┐
      │               │               │
┌─────▼─────┐  ┌─────▼─────┐  ┌─────▼─────┐
│ Child     │  │ Child     │  │ Child     │
│ Model 1   │  │ Model 2   │  │ Model 3   │
│ (Agents)  │  │ (Agents)  │  │ (Agents)  │
└───────────┘  └───────────┘  └───────────┘
```

## Use Cases

### 1. Cognitive Agent Architectures

Build agents with perception, memory, reasoning, and learning:

```netlogo
ask agents [
  ; Perceive
  let percept nnet:forward perception-net sensors
  
  ; Reason
  let decision nnet:forward reasoning-net percept
  
  ; Act
  execute-action decision
  
  ; Learn
  nnet:train-step network inputs targets 0.01
]
```

### 2. Multi-Model Orchestration

Coordinate multiple NetLogo models using neural guidance:

```netlogo
; Collect states from all models
let states ls:report ls:models [ get-state ]

; Neural orchestration
let signal nnet:forward orchestrator states

; Coordinate execution
nnet:orchestrate-models model-ids "neural-guided"
```

### 3. Spatial Computing

Neural networks distributed across spatial environments:

```netlogo
ask patches [
  ; Local neural processing
  let neighborhood-state [state] of neighbors
  let processed nnet:forward patch-network neighborhood-state
  
  ; Update based on neural computation
  update-state processed
]
```

### 4. Evolutionary Learning

Evolve neural network structures through agent evolution:

```netlogo
; Agents with different neural networks compete
ask agents [
  set fitness evaluate-performance
]

; Reproduce best performers with mutations
evolve-networks
```

## Integration with Torch Concepts

| Torch/PyTorch | NNet Extension | NetLogo Context |
|---------------|----------------|-----------------|
| `nn.Linear(in, out)` | `create-layer in out "linear"` | Dense layer |
| `nn.ReLU()` | `create-layer ... "relu"` | ReLU activation |
| `nn.Sequential(...)` | `create-network [layers]` | Network composition |
| `forward(x)` | `forward network inputs` | Forward pass |
| `backward()` | `train-step ...` | Gradient descent |
| `torch.mm()` | `tensor-multiply m1 m2` | Matrix multiply |

## Requirements

- NetLogo 7.0.0 or later
- Java 11 or later
- SBT 1.10.1 (for building)
- LevelSpace extension (for orchestration features)

## Building from Source

```bash
# Clone repository
git clone https://github.com/o9nn/NNetLogos.git
cd NNetLogos

# Build NNet extension
cd extensions/nnet
sbt package

# Run tests
sbt test
```

## Testing

The extension includes comprehensive tests:

```bash
cd extensions/nnet
sbt test
```

Test coverage includes:
- Basic layer and network creation
- Forward propagation
- Activation functions
- Tensor operations
- Symbolic reasoning
- Cognitive state integration

## Performance

- **Small networks** (< 100 neurons): Fast, suitable for many agents
- **Medium networks** (100-1000 neurons): Moderate, use for fewer agents  
- **Large networks** (> 1000 neurons): Slower, use for global policies

Optimization tips:
1. Reuse layers across agents
2. Cache network outputs when possible
3. Use shared networks for similar agents

## Roadmap

### Completed
- ✅ Basic neural network primitives
- ✅ Forward propagation
- ✅ Activation functions (ReLU, Sigmoid, Tanh, Softmax)
- ✅ Tensor operations
- ✅ Training primitives (gradient descent)
- ✅ Neuro-symbolic integration
- ✅ LevelSpace orchestration primitives
- ✅ Example models and documentation

### Planned
- 🔄 Full backpropagation algorithm
- 🔄 Convolutional layers
- 🔄 Recurrent layers (LSTM, GRU)
- 🔄 Attention mechanisms
- 🔄 Advanced optimizers (Adam, RMSprop)
- 🔄 Model persistence (save/load)
- 🔄 GPU acceleration support
- 🔄 Integration with external ML frameworks

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Add tests for new features
4. Submit a pull request

## License

GPL-2.0 - Same as NetLogo core.

## Citation

If you use NNetLogos in your research, please cite:

```bibtex
@software{nnetlogos2026,
  title = {NNetLogos: Neural Network Extension for NetLogo},
  author = {NetLogo Development Team},
  year = {2026},
  url = {https://github.com/o9nn/NNetLogos}
}
```

## Support

- **Forum**: https://forum.netlogo.org
- **Issues**: https://github.com/o9nn/NNetLogos/issues
- **Email**: bugs@ccl.northwestern.edu
- **Gitter**: https://gitter.im/NetLogo/NetLogo

## Related Projects

- **NetLogo**: https://github.com/NetLogo/NetLogo
- **LevelSpace**: https://github.com/NetLogo/LevelSpace
- **PyTorch**: https://pytorch.org
- **Torch7**: https://github.com/torch/torch7

## Acknowledgments

This extension draws inspiration from:
- Torch7 and PyTorch neural network modules
- Cognitive architecture research (ACT-R, SOAR, CLARION)
- Neuro-symbolic AI research
- The NetLogo and LevelSpace development teams

## Authors

- NetLogo Development Team
- Community Contributors

---

**Transform NetLogo into a neuro-symbolic computing platform for advanced agent-based cognitive modeling!** 🚀🧠
