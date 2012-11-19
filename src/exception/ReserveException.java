package exception;

public class ReserveException extends Exception {
	
	private String custormMessage = "";
	
	public String getCustormMessage() {
		return custormMessage;
	}

	public void setCustormMessage(String custormMessage) {
		this.custormMessage = custormMessage;
	}

	public ReserveException(String message){
		super();
		custormMessage = message;
	}
	
	
}
