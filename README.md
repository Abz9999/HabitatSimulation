# Sealife Predator & Prey Simulation — README

## Overview
This project simulates a simple marine ecosystem inspired by the classic “Foxes & Rabbits” model. It features five species:
- Whales
- Sharks
- Sardines
- Clownfish
- Salmon

Predators (whales and sharks) hunt prey (sardines, clownfish, and salmon). The simulation visualises population changes, behaviour, and interactions over time.

## Core Behaviours
- **Predation & competition:** Sharks and whales compete for food. Sharks primarily hunt sardines, while whales primarily hunt clownfish. Both may hunt salmon when preferred prey are unavailable.
- **Movement & detection:** Most movement is random, but predators will pursue prey if nearby.
- **Mating & overcrowding:** Reproduction requires a male–female pair of the same species. Offspring may be lost if surrounding spaces are full.

## Environment & Cycles
- **Day/Night cycle:** Behaviour changes depending on the time of day. For example, sardines sleep at night and remain stationary.
- **Weather/Tide mechanics:** Weather can influence predator success rates. High tide improves sharks’ chances in competition against whales for salmon.

## Known Issues
- Simulations sometimes end early (typically around 100–150 steps) despite parameter adjustments.
- After performing a reset, a new simulation may continue from a state close to the previous run due to lingering state data.
