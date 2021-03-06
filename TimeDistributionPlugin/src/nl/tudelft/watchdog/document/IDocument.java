package nl.tudelft.watchdog.document;

import java.io.Serializable;

public interface IDocument extends Serializable {

	DocumentType getDocumentType();

	String getFileName();

	String getProjectName();

	void setDocumentType(DocumentType type);

}