package models;

public class Alert {

	public String clazz;
	   public String message;
	       public Alert(String clazz, String message){
	       this.clazz = clazz;
	       this.message = message;
	   }
	     
	       public String toString(){
	       return this.clazz+"~"+this.message;
	   }
}
