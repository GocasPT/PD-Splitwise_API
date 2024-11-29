package pt.isec.pd.splitwise.sharedLib.terminal;

public class utils {
	public static String printProgress(long current, long total) {
		int percentage = (int) ((current * 100.0) / total);
		int progressChars = (int) ((60.0 * current) / total);
		return "\r[" +
		       "=".repeat(progressChars) +
		       " ".repeat(60 - progressChars) +
		       String.format("] %d%% (%d/%d bytes)", percentage, current, total);
	}
}
