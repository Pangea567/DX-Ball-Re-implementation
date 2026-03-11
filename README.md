# DX-Ball Re-implementation

A modern re-creation of the classic arcade experience, built entirely in **Java**. This project focuses on high-precision physics, real-time user interaction, and modular object-oriented game design.

## 🚀 Key Features

* **Elastic Collision Physics:** Custom-engineered logic for realistic ball-to-paddle and ball-to-brick reflections.
* **Real-Time Input Handling:** Zero-latency paddle movement responsive to keyboard events.
* **Dynamic Level Scaling:** Efficient management of game objects (bricks, power-ups, and projectiles).
* **OOP Architecture:** Clean separation of concerns using classes for Game Engine, Physics, and UI Rendering.

## 🛠 Technical Stack

* **Language:** Java (JDK 8+)
* **GUI Framework:** Java Swing / AWT
* **Physics Logic:** Vector-based reflection and collision detection algorithms.



## 🎮 Core Mechanics

The game engine operates on a continuous loop:
1.  **Update:** Calculates the new position of the ball based on velocity vectors.
2.  **Check Collisions:** Detects intersections between the ball, paddle, and brick boundaries.
3.  **Handle Input:** Updates paddle position based on user key presses.
4.  **Render:** Redraws the frame to provide a smooth 60 FPS experience.

## 🏁 Quick Start

### Prerequisites
* Java Development Kit (JDK) installed.

### Run the Game
1. Compile the source files:
java OmerFarukKaragozModified.java
