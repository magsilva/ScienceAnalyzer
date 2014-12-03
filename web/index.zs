void processUpload() {
	UploadEvent uploadEvent = (UploadEvent) event;
	org.zkoss.util.media.Media[] medias = uploadEvent.getMedias();
	for (org.zkoss.util.media.Media m : medias) {
		Messagebox.show("File name: " + m.getName(), "Status", Messagebox.OK, Messagebox.OK);
	}
}