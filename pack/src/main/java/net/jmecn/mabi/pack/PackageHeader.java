package net.jmecn.mabi.pack;

public class PackageHeader {
	/******
	 * 512Bytes
	 */
	/**
	 * this tells the client if it is a valid pack file
	 */
	final String head = "PACK";
	final int version = 0x0102;
	/**
	 * file version
	 */
	int revision = 0;
	/**
	 * number of entries in the file
	 */
	int entryCount;
	/**
	 * ? some kind of timestamp, doesn't seem to be unix
	 */
	long fileTime1;
	/**
	 * ? some kind of timestamp, doesn't seem to be unix
	 */
	long fileTime2;
	/**
	 * 480 bytes string
	 */
	String dataPath;// = new byte[480];

	/**********************
	 * 32B
	 */
	/**
	 * number of files in the package
	 */
	int fileCount;
	/**
	 * size of the info header in bytes
	 */
	int headerSize;
	/**
	 * a defined blank space size, for appending
	 */
	int blankSize;
	/**
	 * size of the content
	 */
	int contentSize;
	/**
	 * ?
	 */
	byte[] zero;

	PackageHeader() {
		zero = new byte[16];
	}
}