package practice;

import meshes.Mesh;
import meshes.Renderable;
import meshes.TexturedMesh;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private long window; //handle for the window
    private ShaderProgram shaderProgram;
    private ArrayList<Renderable> objects;

    public Window(int width, int height) {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        this.objects = new ArrayList<>();

        init(width, height);
    }

    private void init(int w, int h) {
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit()) throw new IllegalStateException("Unable to init GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); //window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); //window will be resizeable

        //creating the window now
        window = glfwCreateWindow(w, h, "Hello WORLD!", NULL, NULL);
        if(window == NULL) throw new RuntimeException("Failed to create window");

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        //make a threadstack and push new frame
        try(MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            //pass the window size
            glfwGetWindowSize(window, pWidth, pHeight);

            //get current resolution
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            //center (not necessary tbh)
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            ); //stack frame automatically popped

            //Make opengl context current
            glfwMakeContextCurrent(window);
            // This line is critical for LWJGL's interoperation with GLFW's
            // OpenGL context, or any context that is managed externally.
            // LWJGL detects the context that is current in the current thread,
            // creates the GLCapabilities instance and makes the OpenGL
            // bindings available for use.
            GL.createCapabilities();
            //enable v-sync
            glfwSwapInterval(-1);

            //NOW MAKE IT VISIBLE, DO NOT COMMENT OR DELETE
            glfwShowWindow(window);
        }

        shaderProgram = new ShaderProgram();
//        shaderProgram.attachVertexShader("/shaders/shader_test.v");
//        shaderProgram.attachFragmentShader("/shaders/fragment_test.f");
        shaderProgram.attachVertexShader("/shaders/textured.v");
        shaderProgram.attachFragmentShader("/shaders/textured.f");
        shaderProgram.link();
    }

    public void run() {
        loop();
        dispose();
    }


    private void loop() {

        //set a clear color
        glClearColor(0f, 0.0f, 0f, 0.0f);

        //run until user exits with escape key
        while(!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT); //clear framebuffer

            shaderProgram.bind();

            objects.forEach(o -> o.render());

            glfwSwapBuffers(window); //swap color buffers

            glClearColor(0.0f, 0.0f, 0f, 0.0f);

            //poll window events, key callback will only be invoked here
            glfwPollEvents();

            shaderProgram.unbind();
        }
    }

    public void dispose() {
        //frees window callbacks and destroys
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        //terminates glfw and frees error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

        objects.forEach(o->o.dispose());
    }

    public void add(Renderable o) {objects.add(o);}

    public static void main(String args[]) {
        Window window = new Window(500, 500);

        TexturedMesh rect = new TexturedMesh(
                new float[]
                        {
                                -0.8f, +0.8f, 0,  // ID 0: Top left vertex
                                +0.8f, +0.8f, 0,  // ID 1: Top right vertex
                                -0.8f, -0.8f, 0,  // ID 2: Bottom left vertex
                                +0.8f, -0.8f, 0,  // ID 3: Bottom right vertex
                        },
                new short[]
                {
                        0, 1, 2,  // The indices for the left triangle
                        1, 2, 3   // The indices for the right triangle
                }
        );

        window.add(rect);
        window.run();
    }
}
