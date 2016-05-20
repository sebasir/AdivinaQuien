package model;

import java.io.Serializable;
import java.util.StringTokenizer;

public class Feature implements Serializable{
    private static final long serialVersionUID = 1L;
	private int index;
    private String grupo;
    private String item;
    public Feature(String line){
        StringTokenizer myTok=new StringTokenizer(line);
        index=Integer.parseInt(myTok.nextToken());
        grupo=myTok.nextToken();
        item=myTok.nextToken();
    }

    public int getIndex() {
        return index;
    }
    public String getGrupo(){
        return grupo;
    }
    public String getItem(){
        return item;
    }
}
