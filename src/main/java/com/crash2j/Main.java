package com.crash2j;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

    // Window handle
    private long window;
    private final int WIDTH = 1280;
    private final int HEIGHT = 720;

    public void run() {
        System.out.println("Starting crash:2J Soft-Body Simulator...");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback to print errors to System.err
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // Hide window while setting up
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "crash:2J | Soft-Body Physics Engine", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Key callback - press ESC to exit
        glfwSetKeyCallback(window, (windowHandle, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(windowHandle, true);
            }
        });

        // Center window on screen
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            if (vidmode != null) {
                glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
                );
            }
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        // Critical: Creates LWJGL's OpenGL capabilities bindings
        GL.createCapabilities();

        // Set background clear color (Dark Gray for viewport)
        glClearColor(0.12f, 0.12f, 0.14f, 1.0f);
        glEnable(GL_DEPTH_TEST);
    }

    private void loop() {
        double lastTime = glfwGetTime();
        double physicsAccumulator = 0.0;
        final double PHYSICS_STEP = 1.0 / 1000.0; // 1000Hz Verlet sub-stepping

        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            double frameTime = currentTime - lastTime;
            lastTime = currentTime;

            physicsAccumulator += frameTime;

            // Poll input events
            glfwPollEvents();

            // High-frequency physics updates
            while (physicsAccumulator >= PHYSICS_STEP) {
                updatePhysics((float) PHYSICS_STEP);
                physicsAccumulator -= PHYSICS_STEP;
            }

            // Render loop
            render();

            glfwSwapBuffers(window);
        }
    }

    private void updatePhysics(float dt) {
        // TODO: Call VerletSolver.update(dt) here!
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // TODO: Render low-poly chassis nodes & beams or visual skin
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
