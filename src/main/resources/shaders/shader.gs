#version 330 core

layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

uniform mat4 worldTransform;

out vec3 normal;
out float lf;
out float darken;
out vec3 color;

in gl_PerVertex
{
  vec4 gl_Position;
  vec3 colorV;
} gl_in[];

void main() {

    vec3 a = gl_in[0].gl_Position.xyz-gl_in[1].gl_Position.xyz;
    vec3 b = gl_in[0].gl_Position.xyz-gl_in[2].gl_Position.xyz;

    normal = cross(a,b);
    lf = 1-length(cross(normalize(normal),normalize(vec3(0,0,-1))))*0.5;
    //lf = 0.5+dot(normalize(normal),normalize(vec3(0,0,1)))/2;

    color = gl_in[0].colorV;
    darken = 1/(1+gl_in[0].gl_Position.z*0.03);
    gl_Position = gl_in[0].gl_Position;
    EmitVertex();

    color = gl_in[1].colorV;
    darken = 1/(1+gl_in[1].gl_Position.z*0.03);
    gl_Position = gl_in[1].gl_Position;
    EmitVertex();

    color = gl_in[2].colorV;
    darken = 1/(1+gl_in[2].gl_Position.z*0.03);
    gl_Position = gl_in[2].gl_Position;
    EmitVertex();

    EndPrimitive();

}