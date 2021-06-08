#version 330 core

//const int n = 8;

#define n 16
#define verts 48


layout (points) in;
layout (triangle_strip, max_vertices = verts) out;

uniform mat4 worldTransform;

in gl_PerVertex
{
  vec4 gl_Position;
} gl_in[];

out float r;

void main() {

    for (int i = 0; i < n; i++) {

        gl_Position = gl_in[0].gl_Position;
        r = 0;
        EmitVertex();

        float ang = 3.14159 * 2.0 / n * i;
        vec4 offset = vec4(cos(ang) * 0.3, -sin(ang) * 0.4, 0.0, 0.0);
        gl_Position = gl_in[0].gl_Position + offset;
        r = 1.57;
        EmitVertex();

        ang = 3.14159 * 2.0 / n * (i+1);
        offset = vec4(cos(ang) * 0.3, -sin(ang) * 0.4, 0.0, 0.0);
        gl_Position = gl_in[0].gl_Position + offset;
        r = 1.57;
        EmitVertex();

        EndPrimitive();

    }

}