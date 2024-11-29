package pt.isec.pd.splitwise.sharedLib.network.response;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class ListResponse<T> extends Response {
	private final T[] list;

	public ListResponse(T[] list) {
		super(true);
		this.list = list;
	}

	public ListResponse(String errorDescription) {
		super(false, errorDescription);
		this.list = null;
	}

	public boolean isEmpty() {
		return list == null || list.length == 0;
	}

	public int size() {
		assert list != null;
		return list.length;
	}

	@Override
	public String toString() {
		return "ListResponse [sucess: " + isSuccess() + (!isSuccess() ? ", errorDescription: " + getErrorDescription() : ", list: " + Arrays.toString(
				list)) + "]";
	}
}
