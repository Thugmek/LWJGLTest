#version 330

out vec4 fragColor;

in vec3 normal;
in float lf;
in vec3 color;
in float darken;

uniform vec3 objectColor;


void main()
{
    fragColor = vec4(color[0]*lf*darken, color[1]*lf*darken, color[2]*lf*darken + (1-darken)*0.5, 1.0);
}