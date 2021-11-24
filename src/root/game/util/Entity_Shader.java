package root.game.util;

public interface Entity_Shader {

    default float[][] setupShader(int lightRadius) {
        float[][] shader = new float[lightRadius*2][lightRadius*2];
        for (int i = 0; i < shader.length; i++) {
            for (int j = 0; j < shader[0].length; j++) {

                float a = Vecc2f.distance(i, j, lightRadius, lightRadius);
                if (a<lightRadius){
                    shader[i][j]=(a/lightRadius);
                }else{
                    shader[i][j]=1;
                }
            }
        }
        return shader;
    }
}