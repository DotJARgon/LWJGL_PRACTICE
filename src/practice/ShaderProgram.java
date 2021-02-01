package practice;

import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    int programID;
    int vertexShaderID;
    int fragmentShaderID;

    private FloatBuffer matrix = BufferUtils.createFloatBuffer(16);

    public ShaderProgram() {
        programID = glCreateProgram();
    }

    public void attachVertexShader(String name) {
        // Load the source
        String vertexShaderSource = fromRelative(name);

        // Create the shader and set the source
        vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderID, vertexShaderSource);

        // Compile the shader
        glCompileShader(vertexShaderID);

        // Check for errors
        if (glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE)
            throw new RuntimeException("Error creating vertex shader\n"
                    + glGetShaderInfoLog(vertexShaderID, glGetShaderi(vertexShaderID, GL_INFO_LOG_LENGTH)));

        // Attach the shader
        glAttachShader(programID, vertexShaderID);
    }

    public void attachFragmentShader(String name) {
        // Read the source
        String fragmentShaderSource = fromRelative(name);

        // Create the shader and set the source
        fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderID, fragmentShaderSource);

        // Compile the shader
        glCompileShader(fragmentShaderID);

        // Check for errors
        if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE)
            throw new RuntimeException("Error creating fragment shader\n"
                    + glGetShaderInfoLog(fragmentShaderID, glGetShaderi(fragmentShaderID, GL_INFO_LOG_LENGTH)));

        // Attach the shader
        glAttachShader(programID, fragmentShaderID);
    }

    public void link() {
        // Link this program
        glLinkProgram(programID);

        // Check for linking errors
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE)
            throw new RuntimeException("Unable to link shader program:");
    }

    public void bind() {
        glUseProgram(programID);
    }

    public static void unbind() {
        glUseProgram(0);
    }

    public void dispose() {
        // Unbind the program
        unbind();

        // Detach the shaders
        glDetachShader(programID, vertexShaderID);
        glDetachShader(programID, fragmentShaderID);

        // Delete the shaders
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);

        // Delete the program
        glDeleteProgram(programID);
    }

    public int getID() {
        return programID;
    }

    protected int getUniformLocation(String uniformName) {
        return GL20.glGetUniformLocation(programID, uniformName);
    }

    protected void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(programID, attribute, variableName);
    }

    protected void loadFloat(int location, float value) {
        GL20.glUniform1f(location, value);
    }

    protected void loadVector(int location, Vector3f vector) {
        GL20.glUniform3f(location, vector.x, vector.y, vector.z);
    }

    protected void loadBoolean(int location, boolean value) {
        float tovec = 0;
        if(value) {
            tovec = 1;
        }
        GL20.glUniform1f(location, tovec);
    }

    protected void loadMatrix(int location, Matrix4f value) {
        value.set(matrix);
        matrix.flip();
        glUniformMatrix4fv(location, false, matrix);
    }

    protected void bindAttributes() {
        bindAttribute(0, "position");
        bindAttribute(1, "textureCoords");
    }

    public static String fromRelative(String file) {
        InputStream in = ShaderProgram.class.getResourceAsStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder contents = new StringBuilder();

        String s = null;

        while(true) {
            try {
                if (!((s = reader.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            contents.append(s + "\n");
        }

        return contents.toString();
    }
}