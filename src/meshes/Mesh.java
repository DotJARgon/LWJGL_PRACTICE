package meshes;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Mesh implements Renderable {
    private float[] vertices;
    private float[] colors;
    private short[] indices;

    private int vaoID, vboID, eboID, vboColID;
    private FloatBuffer vBuff, cBuff;
    private ShortBuffer iBuff;

    public Mesh(float[] vertices, float[] colors, short[] indices) {
        this.vertices = vertices;
        this.colors = colors;
        this.indices = indices;

        this.vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();

        vBuff = BufferUtils.createFloatBuffer(vertices.length);
        vBuff.put(vertices).flip();

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vBuff, GL_STATIC_DRAW);

// Point the buffer at location 0, the location we set
// inside the vertex shader. You can use any location
// but the locations should match
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

        cBuff = BufferUtils.createFloatBuffer(colors.length);
        cBuff.put(colors).flip();

        vboColID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboColID);
        glBufferData(GL_ARRAY_BUFFER, cBuff, GL_STATIC_DRAW);

        glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);

        iBuff = BufferUtils.createShortBuffer(indices.length);
        iBuff.put(indices).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, iBuff, GL_STATIC_DRAW);

        // Enable the vertex attribute locations
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
    }

    public void render() {
        // Bind the vertex array and enable our location
        glBindVertexArray(vaoID);

//        // Draw a triangle of 3 vertices
//        glDrawArrays(GL_TRIANGLES, 0, 3);

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0);

        glBindVertexArray(0);
    }

    public void dispose() {
        // Dispose the vertex array
        glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vaoID);

        // Dispose the buffer object
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboID);
        glDeleteBuffers(vboColID);

        // Dispose the element buffer object
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDeleteBuffers(eboID);
    }
}
