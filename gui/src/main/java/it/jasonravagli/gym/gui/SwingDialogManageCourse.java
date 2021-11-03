package it.jasonravagli.gym.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import it.jasonravagli.gym.logic.GymController;
import it.jasonravagli.gym.model.Course;
import it.jasonravagli.gym.model.Member;
import net.miginfocom.swing.MigLayout;
import java.awt.Color;

public class SwingDialogManageCourse extends JDialog implements DialogManageCourse {

	private static final long serialVersionUID = 6456331125860029244L;
	private static final String UNSUPPORTED_OP_MESSAGE = "Operation not supported";

	private final JPanel contentPanel = new JPanel();

	private JTextField textFieldId;
	private JTextField textFieldName;
	private DialogResult result;

	private transient Course course;

	private JButton buttonOk;

	private JLabel labelError;

	public SwingDialogManageCourse(GymController controller) {
		setTitle("Manage Course");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setBounds(100, 100, 380, 191);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("fill",
				"[grow,sizegroup col-s,left][grow,sizegroup col,center][grow,sizegroup col,center]",
				"[grow,sizegroup row][grow,sizegroup row][grow,sizegroup row-s]"));

		JLabel lblNewLabel = new JLabel("Id:");
		contentPanel.add(lblNewLabel, "cell 0 0,alignx trailing");

		textFieldId = new JTextField();
		textFieldId.setName("textFieldId");
		textFieldId.setEnabled(false);
		contentPanel.add(textFieldId, "cell 1 0 2 1,growx");
		textFieldId.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Name:");
		contentPanel.add(lblNewLabel_1, "cell 0 1,alignx trailing");

		textFieldName = new JTextField();
		textFieldName.setName("textFieldName");
		textFieldName.getDocument().addDocumentListener(
				(SimpleDocumentListener) e -> buttonOk.setEnabled(!textFieldName.getText().trim().isEmpty()));
		contentPanel.add(textFieldName, "cell 1 1 2 1,growx");
		textFieldName.setColumns(10);

		labelError = new JLabel(" ");
		labelError.setForeground(Color.RED);
		labelError.setName("labelError");
		contentPanel.add(labelError, "cell 1 2");

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		JButton buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(e -> {
			result = DialogResult.CANCEL;
			setVisible(false);
		});
		buttonCancel.setName("buttonCancel");
		buttonPane.add(buttonCancel);
		buttonOk = new JButton("OK");
		buttonOk.addActionListener(e -> {
			labelError.setText(" ");

			Course newCourse = new Course();
			newCourse.setName(textFieldName.getText().trim());
			if (course == null) {
				newCourse.setId(UUID.randomUUID());
				controller.addCourse(newCourse);
			} else {
				newCourse.setId(course.getId());
				controller.updateCourse(newCourse);
			}
		});
		buttonOk.setEnabled(false);
		buttonOk.setName("buttonOk");
		buttonPane.add(buttonOk);
		getRootPane().setDefaultButton(buttonOk);
	}

	@Override
	public void showCourses(List<Course> courses) {
		throw new UnsupportedOperationException(UNSUPPORTED_OP_MESSAGE);
	}

	@Override
	public void showError(String message) {
		SwingUtilities.invokeLater(() -> labelError.setText(message));
	}

	@Override
	public void showMembers(List<Member> members) {
		throw new UnsupportedOperationException(UNSUPPORTED_OP_MESSAGE);
	}

	@Override
	public void memberAdded(Member member) {
		throw new UnsupportedOperationException(UNSUPPORTED_OP_MESSAGE);
	}

	@Override
	public void memberDeleted(Member member) {
		throw new UnsupportedOperationException(UNSUPPORTED_OP_MESSAGE);
	}

	@Override
	public void memberUpdated(Member updatedMember) {
		throw new UnsupportedOperationException(UNSUPPORTED_OP_MESSAGE);
	}

	@Override
	public void courseAdded(Course course) {
		result = DialogResult.OK;
		setVisible(false);
	}

	@Override
	public void courseDeleted(Course course) {
		throw new UnsupportedOperationException(UNSUPPORTED_OP_MESSAGE);
	}

	@Override
	public void courseUpdated(Course updatedCourse) {
		result = DialogResult.OK;
		setVisible(false);
	}

	@Override
	public DialogResult showDialog() {
		result = DialogResult.CANCEL;
		
		if (course == null) {
			textFieldId.setText("- Autogenerated -");
			textFieldName.setText("");
		} else {
			textFieldId.setText(course.getId().toString());
			textFieldName.setText(course.getName());
		}

		setVisible(true);

		return result;
	}

	@Override
	public void setCourse(Course course) {
		this.course = course;
	}

	@Override
	public void setModalState(boolean modal) {
		setModal(modal);
	}

	/*
	 * Only for testing purposes
	 */
	DialogResult getResult() {
		return result;
	}

	/*
	 * Only for testing purposes
	 */
	void setResult(DialogResult result) {
		this.result = result;
	}

}
