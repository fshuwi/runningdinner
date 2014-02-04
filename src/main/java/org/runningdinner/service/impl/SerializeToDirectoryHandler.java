package org.runningdinner.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.Participant;
import org.runningdinner.service.TempParticipantLocationHandler;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SerializeToDirectoryHandler implements TempParticipantLocationHandler {

	private String tmpUploadDirectory;

	@Override
	public String pushToTempLocation(List<Participant> participants, String uniqueIdentifier) throws IOException {

		Kryo kryo = new Kryo();

		String filename = getUniqueFilename(String.valueOf(Math.abs(participants.hashCode())), uniqueIdentifier);
		final String filepath = tmpUploadDirectory + File.separator + filename;

		DummySerializerHolderClass holderClass = new DummySerializerHolderClass();
		holderClass.setParticipants(participants);

		FileOutputStream fileOut = null;
		Output output = null;

		try {
			fileOut = new FileOutputStream(filepath);
			output = new Output(fileOut);
			kryo.writeObject(output, holderClass);
		}
		finally {
			CoreUtil.closeStream(output);
			CoreUtil.closeStream(fileOut);
		}

		return filepath;
	}

	protected String getUniqueFilename(String objectIdentifier, String uniqueIdentifier) {
		return uniqueIdentifier + "_" + objectIdentifier;
	}

	@Override
	public List<Participant> popFromTempLocation(String location) throws IOException {

		Kryo kryo = new Kryo();

		FileInputStream fileIn = null;
		Input input = null;

		try {
			fileIn = new FileInputStream(location);
			input = new Input(fileIn);
			DummySerializerHolderClass tmpResult = kryo.readObject(input, DummySerializerHolderClass.class);
			return tmpResult.getParticipants();
		}
		finally {
			CoreUtil.closeStream(input);
			CoreUtil.closeStream(fileIn);
		}
	}

	public String getTmpUploadDirectory() {
		return tmpUploadDirectory;
	}

	public void setTmpUploadDirectory(String tmpUploadDirectory) {
		this.tmpUploadDirectory = tmpUploadDirectory;
	}

	static class DummySerializerHolderClass {
		private List<Participant> participants;

		public DummySerializerHolderClass() {
		}

		public List<Participant> getParticipants() {
			return participants;
		}

		public void setParticipants(List<Participant> participants) {
			this.participants = participants;
		}

	}

}
