#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec3 col;

uniform mat4 objectTransform;
uniform mat4 worldTransform;
uniform mat4 projectionTransform;

out gl_PerVertex
{
  vec4 gl_Position;
  vec3 colorV;
};


void main()
{
    colorV = col;
    //colorV = vec3(1,0,0);
    vec4 pos = projectionTransform * worldTransform * objectTransform * vec4(position,1);
    gl_Position = pos;
}