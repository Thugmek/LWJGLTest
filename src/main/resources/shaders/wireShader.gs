#version 330 core

layout (triangles) in;
layout (line_strip, max_vertices = 4) out;

uniform mat4 worldTransform;

in gl_PerVertex
{
  vec4 gl_Position;
  vec3 colorV;
} gl_in[];

void main() {

    gl_Position = gl_in[0].gl_Position;
    EmitVertex();

    gl_Position = gl_in[1].gl_Position;
    EmitVertex();

    gl_Position = gl_in[2].gl_Position;
    EmitVertex();

    gl_Position = gl_in[0].gl_Position;
    EmitVertex();

    EndPrimitive();

}