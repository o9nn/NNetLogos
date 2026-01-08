; Cognitive Architecture Demo
; Demonstrates neuro-symbolic cognitive architecture for agent decision-making

extensions [nnet]

globals [
  perception-network
  reasoning-network
]

turtles-own [
  cognitive-state
  memory
  current-goal
]

to setup
  clear-all
  
  ; Create perception network (processes sensory input)
  let p1 nnet:create-layer 8 16 "relu"
  let p2 nnet:create-layer 16 8 "tanh"
  set perception-network nnet:create-network (list p1 p2)
  
  ; Create reasoning network (makes decisions)
  let r1 nnet:create-layer 8 12 "relu"
  let r2 nnet:create-layer 12 4 "sigmoid"
  set reasoning-network nnet:create-network (list r1 r2)
  
  ; Create agents with cognitive architectures
  create-turtles 30 [
    setxy random-xcor random-ycor
    set shape "circle"
    set color blue
    set memory []
    set current-goal "explore"
  ]
  
  ; Create some targets
  create-turtles 10 [
    setxy random-xcor random-ycor
    set shape "star"
    set color yellow
    set size 2
  ]
  
  reset-ticks
end

to go
  ask turtles with [shape = "circle"] [
    ; Cognitive processing cycle
    perceive-environment
    update-cognitive-state
    reason-and-decide
    execute-action
  ]
  
  tick
end

to perceive-environment  ; turtle procedure
  ; Gather sensory information about environment
  let nearby-agents count turtles in-radius 5
  let nearby-targets count turtles with [shape = "star"] in-radius 5
  let heading-val heading / 360
  let energy-level random-float 1.0
  
  ; Create sensory input vector (8 dimensions)
  let sensory-input (list
    nearby-agents / 10
    nearby-targets / 10
    heading-val
    energy-level
    xcor / max-pxcor
    ycor / max-pycor
    random-float 0.5  ; noise
    random-float 0.5  ; noise
  )
  
  ; Process through perception network
  set cognitive-state nnet:forward perception-network sensory-input
end

to update-cognitive-state  ; turtle procedure
  ; Integrate perception with memory
  let integrated-state nnet:cognitive-state cognitive-state
  
  ; Update memory (keep last 5 states)
  set memory lput integrated-state memory
  if length memory > 5 [
    set memory but-first memory
  ]
  
  ; Visualize cognitive state (color represents internal state)
  set color scale-color blue (item 0 cognitive-state) -1 1
end

to reason-and-decide  ; turtle procedure
  ; Use reasoning network to make decisions
  let decision-output nnet:forward reasoning-network cognitive-state
  
  ; Apply symbolic reasoning to choose goal
  let goal-choice nnet:symbolic-reasoning decision-output "max"
  
  ; Determine current goal based on reasoning
  ifelse goal-choice > 0.7 [
    set current-goal "seek-target"
  ] [
    ifelse goal-choice > 0.4 [
      set current-goal "explore"
    ] [
      set current-goal "avoid"
    ]
  ]
end

to execute-action  ; turtle procedure
  ; Execute action based on current goal
  if current-goal = "seek-target" [
    ; Move toward nearest target
    let target min-one-of turtles with [shape = "star"] [distance myself]
    if target != nobody [
      face target
      fd 0.5
    ]
  ]
  
  if current-goal = "explore" [
    ; Random exploration with forward bias
    rt random 60 - 30
    fd 1
  ]
  
  if current-goal = "avoid" [
    ; Avoid crowded areas
    let nearby count turtles in-radius 3
    if nearby > 5 [
      rt 180
      fd 1
    ]
  ]
  
  ; Stay in bounds
  if abs xcor > max-pxcor - 1 or abs ycor > max-pycor - 1 [
    rt 180
  ]
end

to demonstrate-tensor-ops
  ; Demonstrate tensor operations
  print "Tensor Addition:"
  print nnet:tensor-add [1 2 3] [4 5 6]
  
  print "Matrix Multiplication:"
  let matrix1 [[1 2] [3 4]]
  let matrix2 [[5 6] [7 8]]
  print nnet:tensor-multiply matrix1 matrix2
  
  print "Matrix Transpose:"
  print nnet:tensor-transpose [[1 2 3] [4 5 6]]
end

to analyze-cognitive-patterns
  ; Analyze emerging cognitive patterns
  ask turtles with [shape = "circle"] [
    print (word "Agent " who ":")
    print (word "  Current Goal: " current-goal)
    print (word "  Cognitive State: " cognitive-state)
    if length memory > 0 [
      print (word "  Memory Length: " length memory)
    ]
  ]
end
