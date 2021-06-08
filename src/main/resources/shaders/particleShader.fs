#version 330

out vec4 fragColor;

in float r;

void main()
{
    fragColor = vec4(1,1,1,0.1*sin(r));
}