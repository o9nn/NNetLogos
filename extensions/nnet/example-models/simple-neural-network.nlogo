; Simple Neural Network Demo
; This model demonstrates basic neural network operations with the NNet extension

extensions [nnet]

globals [
  my-network
  layer1-id
  layer2-id
  layer3-id
]

to setup
  clear-all
  
  ; Create a 3-layer neural network
  ; Input: 2 neurons, Hidden: 4 neurons, Output: 1 neuron
  set layer1-id nnet:create-layer 2 4 "relu"
  set layer2-id nnet:create-layer 4 2 "relu"
  set layer3-id nnet:create-layer 2 1 "sigmoid"
  
  set my-network nnet:create-network (list layer1-id layer2-id layer3-id)
  
  ; Create some turtles to visualize
  create-turtles 50 [
    setxy random-xcor random-ycor
    set shape "circle"
  ]
  
  reset-ticks
end

to go
  ask turtles [
    ; Get inputs based on position
    let input-x xcor / max-pxcor
    let input-y ycor / max-pycor
    
    ; Forward pass through network
    let output nnet:forward my-network (list input-x input-y)
    let activation item 0 output
    
    ; Color based on network output
    set color scale-color red activation 0 1
    set size 0.5 + activation
    
    ; Move randomly
    rt random 30 - 15
    fd 0.5
  ]
  
  tick
end

to train-network
  ; Train on XOR-like pattern
  let training-data [
    [[0 0] [0]]
    [[0 1] [1]]
    [[1 0] [1]]
    [[1 1] [0]]
  ]
  
  repeat 100 [
    foreach training-data [ example ->
      let inputs item 0 example
      let targets item 1 example
      nnet:train-step my-network inputs targets 0.1
    ]
  ]
  
  print "Training complete!"
end

to test-network
  ; Test the network on various inputs
  print "Testing network:"
  print (word "Input [0 0]: " nnet:forward my-network [0 0])
  print (word "Input [0 1]: " nnet:forward my-network [0 1])
  print (word "Input [1 0]: " nnet:forward my-network [1 0])
  print (word "Input [1 1]: " nnet:forward my-network [1 1])
end

to demonstrate-activations
  ; Show different activation functions
  let input-vals n-values 20 [? / 10 - 1]  ; -1 to 1
  
  print "ReLU:"
  print nnet:relu input-vals
  
  print "Sigmoid:"
  print nnet:sigmoid input-vals
  
  print "Tanh:"
  print nnet:tanh input-vals
end
