package pt.isec.pd.splitwise.sharedLib.network;

import java.io.Serializable;

public record Heartbeat(int version, int tcpPort, String query, Object... params) implements Serializable {
	public static final int BUFFER_SIZE = 8192;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Heartbeat[ ");
		sb.append("version: ").append(version);
		sb.append(", tcpPort: ").append(tcpPort);
		if (query != null) sb.append(",\n\tquery: ").append(query);
		if (params.length > 0) {
			sb.append(",\n\tparams: [ ");
			for (Object param : params) {
				sb.append(param).append(", ");
			}
			sb.setLength(sb.length() - 2);
			sb.append("]\n");
		}
		sb.append(" ]");

		return sb.toString();
	}
}