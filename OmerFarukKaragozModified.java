import java.awt.*;
import java.awt.event.KeyEvent;

public class OmerFarukKaragozModified {
    public static void main(String[] args) {
        // Canvas properties
        double xScale = 800.0, yScale = 400.0;
        StdDraw.setCanvasSize(800, 400);
        StdDraw.setXscale(0.0, xScale);
        StdDraw.setYscale(0.0, yScale);

        StdDraw.enableDoubleBuffering();

        // Shooting angle
        double shootingAngle = 90;  // Initially 90 degrees upward
        double radianOfAngle = Math.toRadians(shootingAngle);

        // For launching the ball
        boolean ballLaunched = false;
        boolean spacePressed = false;  // Track space key state

        // For game score
        int score = 0;

        // For game end
        boolean gameOver = false;
        boolean victory = false;

        // Game active or not
        boolean isPaused = false;

        // Ball movement
        double ballSpeed = 5;   // Magnitude of the ball velocity
        double ballSpeed_X = 0;
        double ballSpeed_Y = 0;
        double ballPosX = 400, ballPosY = 18.01;   // Initial position of the ball
        double ballRadius = 8;   // Ball radius
        Color ballColor = new Color(15, 82, 186);   // Color of the ball

        // Paddle variables
        double paddlePosX = 400, paddlePosY = 5; // Initial position of the center of the paddle
        double paddleHalfwidth = 60; // Paddle halfwidth
        double paddleHalfheight = 5; // Paddle halfheight
        double paddleSpeed = 20; // Paddle speed
        Color paddleColor = new Color(128, 128, 128); // Paddle color

        // Brick variables
        double brickHalfwidth = 50; // Brick halfwidth
        double brickHalfheight = 10; // Brick halfheight

// Color array for bricks - Yeni renkler
        Color[] colors = {new Color(0, 128, 128), new Color(64, 224, 208),
                new Color(72, 209, 204), new Color(32, 178, 170),
                new Color(95, 158, 160), new Color(70, 130, 180)};

// 2D array to store center coordinates of bricks in the format {x, y}
        double[][] brickCoordinates = new double[][]{
                {200, 350}, {400, 350}, {600, 350},
                {100, 320}, {300, 320}, {500, 320}, {700, 320},
                {200, 290}, {400, 290}, {600, 290},
                {100, 260}, {300, 260}, {500, 260}, {700, 260},
                {200, 230}, {400, 230}, {600, 230},
                {100, 200}, {300, 200}, {500, 200}, {700, 200},
                {200, 170}, {400, 170}, {600, 170}};

// Brick colors
        Color[] brickColors = new Color[]{
                colors[0], colors[1], colors[2],
                colors[3], colors[4], colors[5], colors[0],
                colors[1], colors[2], colors[3],
                colors[4], colors[5], colors[0], colors[1],
                colors[2], colors[3], colors[4],
                colors[5], colors[0], colors[1], colors[2],
                colors[3], colors[4], colors[5]};


        // For brick visibility
        boolean[] brickVisible = new boolean[brickCoordinates.length];

        // Initialize all bricks as visible
        for (int i = 0; i < brickVisible.length; i++) {
            brickVisible[i] = true;
        }

        while (true) {
            // Check for space to pause only if the ball is already launched
            if (ballLaunched) {
                isPaused = pauseGame(isPaused, ballLaunched, gameOver, victory, shootingAngle);
            }

            if (!isPaused) {
                if (!ballLaunched) {
                    // Simply check if space is pressed to launch the ball
                    if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                        // Launch the ball immediately
                        ballLaunched = true;
                        ballSpeed_X = ballSpeed * Math.cos(radianOfAngle);
                        ballSpeed_Y = ballSpeed * Math.sin(radianOfAngle);

                        // Wait until space is released to prevent accidental double-triggers
                        while (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                            StdDraw.pause(10);
                        }
                    }

                    // Only adjust angle if ball not launched
                    if (!ballLaunched) {
                        double[] result = adjustShootingAngle(shootingAngle, radianOfAngle);
                        shootingAngle = result[0];
                        radianOfAngle = result[1];
                    }

                    drawScene(isPaused, ballLaunched, shootingAngle, score, ballPosX, ballPosY, ballRadius, ballColor,
                            paddlePosX, paddlePosY, paddleHalfwidth, paddleHalfheight, paddleColor,
                            brickCoordinates, brickHalfwidth, brickHalfheight, brickColors, brickVisible, radianOfAngle);
                } else {
                    double[] ballMovementResult = ballMovement(ballPosX, ballPosY, ballRadius, ballSpeed_X, ballSpeed_Y,
                            paddlePosX, paddlePosY, paddleHalfwidth, paddleHalfheight);
                    ballPosX = ballMovementResult[0];
                    ballPosY = ballMovementResult[1];
                    ballSpeed_X = ballMovementResult[2];
                    ballSpeed_Y = ballMovementResult[3];

                    // then check paddle collision
                    double[] paddleCollisionResult = paddleCollision(ballPosX, ballPosY, ballRadius, ballSpeed_X, ballSpeed_Y,
                            paddlePosX, paddlePosY, paddleHalfwidth, paddleHalfheight);
                    ballSpeed_X = paddleCollisionResult[0];
                    ballSpeed_Y = paddleCollisionResult[1];

                    // check brick collision
                    int[] brickResult = brickCollision(ballPosX, ballPosY, ballRadius, ballSpeed_X, ballSpeed_Y,
                            brickCoordinates, brickHalfwidth, brickHalfheight,
                            brickVisible, score);

                    score = brickResult[0];
                    // arrange velocity
                    if (brickResult[1] == 1) {
                        ballSpeed_X = -ballSpeed_X;
                    }
                    if (brickResult[2] == 1) {
                        ballSpeed_Y = -ballSpeed_Y;
                    }

                    // paddle movement
                    paddlePosX = paddleMovement(paddlePosX, paddleHalfwidth, paddleSpeed);

                    // check game status
                    gameOver = checkGameOver(ballPosY);
                    victory = checkVictory(brickVisible);

                    if (gameOver || victory) {
                        isPaused = true;
                    }

                    drawScene(isPaused, ballLaunched, shootingAngle, score, ballPosX, ballPosY, ballRadius, ballColor,
                            paddlePosX, paddlePosY, paddleHalfwidth, paddleHalfheight, paddleColor,
                            brickCoordinates, brickHalfwidth, brickHalfheight, brickColors, brickVisible, radianOfAngle);
                }
            } else {
                if (victory || gameOver) {
                    displayEndGameMessage(victory, gameOver, score);
                } else {
                    pauseMessage(isPaused, score);
                }
            }
            StdDraw.pause(20);
        }
    }

    static double paddleMovement(double paddlePosX, double paddleHalfwidth, double paddleSpeed) {
        if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT) && paddlePosX - paddleHalfwidth > 0) {
            paddlePosX -= paddleSpeed;
        } else if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT) && paddlePosX + paddleHalfwidth < 800) {
            paddlePosX += paddleSpeed;
        }
        return paddlePosX;
    }

    static void drawScene(boolean isPaused, boolean ballLaunched, double shootingAngle, int score,
                          double ballPosX, double ballPosY, double ballRadius, Color ballColor,
                          double paddlePosX, double paddlePosY, double paddleHalfwidth, double paddleHalfheight, Color paddleColor,
                          double[][] brickCoordinates, double brickHalfwidth, double brickHalfheight,
                          Color[] brickColors, boolean[] brickVisible, double radianOfAngle) {
        StdDraw.clear(StdDraw.WHITE); // Clear background

        // Draw paddle
        StdDraw.setPenColor(paddleColor);
        StdDraw.filledRectangle(paddlePosX, paddlePosY, paddleHalfwidth, paddleHalfheight);

        // Draw bricks (only visible ones)
        for (int i = 0; i < brickCoordinates.length; i++) {
            if (brickVisible[i]) {
                StdDraw.setPenColor(brickColors[i]);
                StdDraw.filledRectangle(brickCoordinates[i][0], brickCoordinates[i][1], brickHalfwidth, brickHalfheight);
            }
        }

        // Draw ball
        StdDraw.setPenColor(ballColor);
        StdDraw.filledCircle(ballPosX, ballPosY, ballRadius);

        // If game is paused, display "PAUSED GAME" text
        if (isPaused) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setFont(new Font("Arial", Font.BOLD, 40));
            StdDraw.text(400, 200, "PAUSED GAME");
        }

        // Before launching, show angle and direction line
        if (!ballLaunched) {
            double lineLength = 50;
            double endX = ballPosX + lineLength * Math.cos(radianOfAngle);
            double endY = ballPosY + lineLength * Math.sin(radianOfAngle);

            StdDraw.setPenColor(Color.RED);
            StdDraw.line(ballPosX, ballPosY, endX, endY); // Line showing shooting direction

            // Display angle on screen
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setFont(new Font("Arial", Font.BOLD, 20));
            StdDraw.text(100, 380, "Angle: " + (int) shootingAngle + "°");
        }

        // Display score on screen
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 16));
        StdDraw.text(700, 380, "Score: " + score);

        StdDraw.show(); // Show updated screen
    }

    static double[] ballMovement(double ballPosX, double ballPosY, double ballRadius, double ballSpeed_X, double ballSpeed_Y,
                                 double paddlePosX, double paddlePosY, double paddleHalfwidth, double paddleHalfheight) {
        // check collision
        if (ballPosX + ballSpeed_X > 800 - ballRadius || ballPosX + ballSpeed_X < ballRadius) {
            ballSpeed_X = -ballSpeed_X;
        }

        // check collision
        if (ballPosY + ballSpeed_Y > 400 - ballRadius) {
            ballSpeed_Y = -ballSpeed_Y;
        }

        // update location of ball
        ballPosX = ballPosX + ballSpeed_X;
        ballPosY = ballPosY + ballSpeed_Y;

        return new double[] {ballPosX, ballPosY, ballSpeed_X, ballSpeed_Y};
    }

    static double[] paddleCollision(double ballPosX, double ballPosY, double ballRadius, double ballSpeed_X, double ballSpeed_Y,
                                    double paddlePosX, double paddlePosY, double paddleHalfwidth, double paddleHalfheight) {
        double paddleLeft = paddlePosX - paddleHalfwidth;
        double paddleRight = paddlePosX + paddleHalfwidth;
        double paddleTop = paddlePosY + paddleHalfheight;
        double paddleBottom = paddlePosY - paddleHalfheight;

        // calculate location
        double nextBallPosX = ballPosX + ballSpeed_X;
        double nextBallPosY = ballPosY + ballSpeed_Y;

        // new speed
        double newSpeedX = ballSpeed_X;
        double newSpeedY = ballSpeed_Y;

        // check collision
        if (nextBallPosX + ballRadius >= paddleLeft &&
                nextBallPosX - ballRadius <= paddleRight &&
                nextBallPosY - ballRadius <= paddleTop &&
                nextBallPosY + ballRadius >= paddleBottom) {


            double collisionPointX = 0;
            double collisionPointY = 0;
            double normalX = 0;
            double normalY = 0;

            // find nearest corner
            // check 1. corner
            boolean isCorner = false;
            double minCornerDist = Double.MAX_VALUE;
            int cornerIndex = -1;

            // corner points
            double[] cornerX = {paddleLeft, paddleRight, paddleRight, paddleLeft};
            double[] cornerY = {paddleTop, paddleTop, paddleBottom, paddleBottom};

            for (int i = 0; i < 4; i++) {
                double distX = ballPosX - cornerX[i];
                double distY = ballPosY - cornerY[i];
                double distance = Math.sqrt(distX * distX + distY * distY);

                if (distance < minCornerDist) {
                    minCornerDist = distance;
                    cornerIndex = i;
                }
            }

            // check whether the ball near the corner
            if (minCornerDist <= ballRadius * 1.5) {
                isCorner = true;
                collisionPointX = cornerX[cornerIndex];
                collisionPointY = cornerY[cornerIndex];

                // Normal vector
                normalX = ballPosX - collisionPointX;
                normalY = ballPosY - collisionPointY;

                // Normal vector calculation
                double normalLength = Math.sqrt(normalX * normalX + normalY * normalY);
                if (normalLength > 0) {
                    normalX /= normalLength;
                    normalY /= normalLength;
                } else {
                    // if there is no collision these are assumpted normals
                    normalX = 0;
                    normalY = 1;
                }
            }
            // edge check
            else {

                boolean ballCenterInside =
                        ballPosX >= paddleLeft &&
                                ballPosX <= paddleRight &&
                                ballPosY >= paddleBottom &&
                                ballPosY <= paddleTop;

                if (ballCenterInside) {
                    // find nearest edge
                    double distToTop = paddleTop - ballPosY;
                    double distToBottom = ballPosY - paddleBottom;
                    double distToLeft = ballPosX - paddleLeft;
                    double distToRight = paddleRight - ballPosX;

                    double minDist = Math.min(Math.min(distToTop, distToBottom), Math.min(distToLeft, distToRight));

                    if (minDist == distToTop) {
                        normalX = 0;
                        normalY = 1;
                        collisionPointX = ballPosX;
                        collisionPointY = paddleTop;
                    } else if (minDist == distToBottom) {
                        normalX = 0;
                        normalY = -1;
                        collisionPointX = ballPosX;
                        collisionPointY = paddleBottom;
                    } else if (minDist == distToLeft) {
                        normalX = -1;
                        normalY = 0;
                        collisionPointX = paddleLeft;
                        collisionPointY = ballPosY;
                    } else {
                        normalX = 1;
                        normalY = 0;
                        collisionPointX = paddleRight;
                        collisionPointY = ballPosY;
                    }
                } else {


                    double nearestX = Math.max(paddleLeft, Math.min(ballPosX, paddleRight));
                    double nearestY = Math.max(paddleBottom, Math.min(ballPosY, paddleTop));

                    collisionPointX = nearestX;
                    collisionPointY = nearestY;

                    //normal vector
                    normalX = ballPosX - collisionPointX;
                    normalY = ballPosY - collisionPointY;


                    double normalLength = Math.sqrt(normalX * normalX + normalY * normalY);
                    if (normalLength > 0) {
                        normalX /= normalLength;
                        normalY /= normalLength;
                    } else {

                        normalX = 0;
                        normalY = 1;
                    }
                }
            }


            if (normalY > 0.9 && !isCorner) {
                double tangentX = -normalY;
                double tangentY = normalX;

                // calculate
                double hitPosition = (ballPosX - paddlePosX) / paddleHalfwidth;

                double dotProductNormal = ballSpeed_X * normalX + ballSpeed_Y * normalY;
                double dotProductTangent = ballSpeed_X * tangentX + ballSpeed_Y * tangentY;

                double reflectedNormalComponent = -dotProductNormal;

                double adjustedTangentComponent = dotProductTangent + hitPosition * 2.0;

                // calculate new velocities
                newSpeedX = reflectedNormalComponent * normalX + adjustedTangentComponent * tangentX;
                newSpeedY = reflectedNormalComponent * normalY + adjustedTangentComponent * tangentY;
            } else {
                // standart reflection
                double dotProduct = ballSpeed_X * normalX + ballSpeed_Y * normalY;


                newSpeedX = ballSpeed_X - 2 * dotProduct * normalX;
                newSpeedY = ballSpeed_Y - 2 * dotProduct * normalY;
            }

            // speed
            double maxSpeed = 8.0;
            double currentSpeed = Math.sqrt(newSpeedX * newSpeedX + newSpeedY * newSpeedY);

            if (currentSpeed > maxSpeed) {

                double scale = maxSpeed / currentSpeed;
                newSpeedX *= scale;
                newSpeedY *= scale;
            }

            // Minimum dikey hız kontrolü
            double minVerticalSpeed = 1.0;
            if (Math.abs(newSpeedY) < minVerticalSpeed) {
                newSpeedY = Math.signum(newSpeedY) * minVerticalSpeed;
            }
        }

        return new double[] {newSpeedX, newSpeedY};
    }
    static int[] brickCollision(double ballPosX, double ballPosY, double ballRadius, double ballSpeed_X, double ballSpeed_Y,
                                double[][] brickCoordinates, double brickHalfwidth, double brickHalfheight,
                                boolean[] brickVisible, int score) {
        // Initialize result array: [0] = updated score, [1] = horizontal collision flag, [2] = vertical collision flag
        int[] result = new int[3];
        result[0] = score; // Start with current score

        // Check collision with each brick
        for (int i = 0; i < brickVisible.length; i++) {
            if (!brickVisible[i]) continue; // Skip invisible bricks

            // Get brick position
            double brickX = brickCoordinates[i][0];
            double brickY = brickCoordinates[i][1];

            // Calculate brick boundaries
            double brickLeft = brickX - brickHalfwidth;
            double brickRight = brickX + brickHalfwidth;
            double brickTop = brickY + brickHalfheight;
            double brickBottom = brickY - brickHalfheight;

            // Find closest point on brick to ball center (this is the exact collision point)
            double closestX = Math.max(brickLeft, Math.min(ballPosX, brickRight));
            double closestY = Math.max(brickBottom, Math.min(ballPosY, brickTop));

            // Calculate distance from closest point to ball center
            double distanceX = ballPosX - closestX;
            double distanceY = ballPosY - closestY;
            double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);

            // Check if collision occurred
            if (distance <= ballRadius) {
                // Mark brick as invisible
                brickVisible[i] = false;

                // Increase score
                result[0] += 10;

                // Determine if this is a corner collision or a flat surface collision
                boolean isCornerCollision = false;

                // Check if the collision point is at one of the corners of the brick
                if ((Math.abs(closestX - brickLeft) < 0.01 || Math.abs(closestX - brickRight) < 0.01) &&
                        (Math.abs(closestY - brickTop) < 0.01 || Math.abs(closestY - brickBottom) < 0.01)) {
                    isCornerCollision = true;
                }

                // Handle collision based on collision type
                if (isCornerCollision) {
                    // CORNER COLLISION - As described in assignment

                    // 1. Find exact collision point (we already have it as closestX, closestY)
                    double collisionPointX = closestX;
                    double collisionPointY = closestY;

                    // 2. Calculate normal vector from collision point to ball center
                    double normalX = ballPosX - collisionPointX;
                    double normalY = ballPosY - collisionPointY;

                    // 3. Normalize the normal vector
                    double normalLength = Math.sqrt(normalX * normalX + normalY * normalY);
                    if (normalLength > 0) {
                        normalX /= normalLength;
                        normalY /= normalLength;
                    } else {
                        // Default normals in rare case
                        normalX = 0;
                        normalY = 1;
                    }

                    // 4. Calculate tangent vector (perpendicular to normal)
                    double tangentX = -normalY;
                    double tangentY = normalX;

                    // 5. Decompose velocity into normal and tangential components
                    double dotProductNormal = ballSpeed_X * normalX + ballSpeed_Y * normalY;
                    double dotProductTangent = ballSpeed_X * tangentX + ballSpeed_Y * tangentY;

                    // 6. Reverse normal component (bounce) and keep tangential component
                    double newDotProductNormal = -dotProductNormal;

                    // 7. Reconstruct velocity from components
                    double newSpeedX = newDotProductNormal * normalX + dotProductTangent * tangentX;
                    double newSpeedY = newDotProductNormal * normalY + dotProductTangent * tangentY;

                    // Set ball speed directly instead of using flags
                    ballSpeed_X = newSpeedX;
                    ballSpeed_Y = newSpeedY;

                    // Update collision flags based on dominant direction
                    if (Math.abs(newSpeedX - ballSpeed_X) > Math.abs(newSpeedY - ballSpeed_Y)) {
                        result[1] = 1; // Horizontal collision had more impact
                    } else {
                        result[2] = 1; // Vertical collision had more impact
                    }
                } else {
                    // FLAT SURFACE COLLISION

                    // Determine which side of the brick was hit
                    boolean hitTop = Math.abs(ballPosY - brickTop) <= ballRadius && ballPosX >= brickLeft && ballPosX <= brickRight;
                    boolean hitBottom = Math.abs(ballPosY - brickBottom) <= ballRadius && ballPosX >= brickLeft && ballPosX <= brickRight;
                    boolean hitLeft = Math.abs(ballPosX - brickLeft) <= ballRadius && ballPosY >= brickBottom && ballPosY <= brickTop;
                    boolean hitRight = Math.abs(ballPosX - brickRight) <= ballRadius && ballPosY >= brickBottom && ballPosY <= brickTop;

                    // Apply appropriate reflection based on which side was hit
                    if (hitTop || hitBottom) {
                        ballSpeed_Y = -ballSpeed_Y;
                        result[2] = 1; // Vertical collision
                    }
                    if (hitLeft || hitRight) {
                        ballSpeed_X = -ballSpeed_X;
                        result[1] = 1; // Horizontal collision
                    }

                    // If no clear side was hit, treat it as a corner collision
                    if (!(hitTop || hitBottom || hitLeft || hitRight)) {
                        // Calculate normal vector from collision point to ball center
                        double normalX = ballPosX - closestX;
                        double normalY = ballPosY - closestY;

                        // Normalize the normal vector
                        double normalLength = Math.sqrt(normalX * normalX + normalY * normalY);
                        if (normalLength > 0) {
                            normalX /= normalLength;
                            normalY /= normalLength;
                        } else {
                            normalX = 0;
                            normalY = 1;
                        }

                        // Calculate tangent vector
                        double tangentX = -normalY;
                        double tangentY = normalX;

                        // Decompose velocity
                        double dotProductNormal = ballSpeed_X * normalX + ballSpeed_Y * normalY;
                        double dotProductTangent = ballSpeed_X * tangentX + ballSpeed_Y * tangentY;

                        // Reverse normal component
                        double newDotProductNormal = -dotProductNormal;

                        // Reconstruct velocity
                        ballSpeed_X = newDotProductNormal * normalX + dotProductTangent * tangentX;
                        ballSpeed_Y = newDotProductNormal * normalY + dotProductTangent * tangentY;

                        // Update collision flags based on dominant direction
                        if (Math.abs(normalX) > Math.abs(normalY)) {
                            result[1] = 1; // Horizontal collision had more impact
                        } else {
                            result[2] = 1; // Vertical collision had more impact
                        }
                    }
                }

                // Apply minimum velocity to prevent sticking
                double currentSpeed = Math.sqrt(ballSpeed_X * ballSpeed_X + ballSpeed_Y * ballSpeed_Y);
                double minSpeed = 1.0;
                if (currentSpeed < minSpeed) {
                    double scaleFactor = minSpeed / currentSpeed;
                    ballSpeed_X *= scaleFactor;
                    ballSpeed_Y *= scaleFactor;
                }

                // Only process one collision per frame
                break;
            }
        }

        return result;
    }
    static boolean pauseGame(boolean isPaused, boolean ballLaunched, boolean gameOver, boolean victory, double shootingAngle) {
        if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
            while (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                // Wait until space key is released
                StdDraw.pause(10);
            }

            // Only handle pause/unpause when the ball is launched and game is not over
            if (ballLaunched && !gameOver && !victory) {
                // If ball is already launched, pause the game
                return !isPaused;
            }
        }
        return isPaused;
    }
    static void pauseMessage(boolean isPaused, int score) {
        if (isPaused) {
            StdDraw.clear(StdDraw.WHITE);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setFont(new Font("Arial", Font.BOLD, 40));
            StdDraw.text(400, 200, "GAME PAUSED");

            // Draw score during pause
            StdDraw.setFont(new Font("Arial", Font.BOLD, 20));
            StdDraw.text(400, 150, "Score: " + score);

            StdDraw.show();
        }
    }

    static double[] adjustShootingAngle(double shootingAngle, double radianOfAngle) {
        if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT) && shootingAngle < 180) {
            shootingAngle += 1;  // Shift angle to the left
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT) && shootingAngle > 0) {
            shootingAngle -= 1;  // Shift angle to the right
        }

        // When angle is updated, also update the radian value
        radianOfAngle = Math.toRadians(shootingAngle);

        return new double[] {shootingAngle, radianOfAngle};
    }

    static boolean checkGameOver(double ballPosY) {
        // Check if ball fell below screen
        return ballPosY < 0;
    }

    static boolean checkVictory(boolean[] brickVisible) {
        // Check if all bricks are removed
        for (boolean visible : brickVisible) {
            if (visible) {
                return false;
            }
        }
        return true;
    }

    static void displayEndGameMessage(boolean victory, boolean gameOver, int score) {
        StdDraw.clear(StdDraw.WHITE);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 40));

        if (victory) {
            StdDraw.text(400, 220, "VICTORY!");
        } else if (gameOver) {
            StdDraw.text(400, 220, "GAME OVER!");
        }

        StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
        StdDraw.text(400, 180, "Score: " + score);
        StdDraw.show();
    }
}