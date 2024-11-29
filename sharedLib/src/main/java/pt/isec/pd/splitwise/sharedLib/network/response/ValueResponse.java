package pt.isec.pd.splitwise.sharedLib.network.response;

import lombok.Getter;

@Getter
public class ValueResponse<T> extends Response {
	private final T value;

	public ValueResponse(T value) {
		super(true);
		this.value = value;
	}

	public ValueResponse(String errorDescription) {
		super(false, errorDescription);
		this.value = null;
	}

	@Override
	public String toString() {
		return "ValueResponse [sucess: " + isSuccess() + (!isSuccess() ? ", errorDescription: " + getErrorDescription() : ", value: " + value) + "]";
	}
}
