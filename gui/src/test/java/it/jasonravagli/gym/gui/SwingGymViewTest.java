package it.jasonravagli.gym.gui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.data.Index;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JLabelFixture;
import org.assertj.swing.fixture.JListFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import it.jasonravagli.gym.logic.GymController;
import it.jasonravagli.gym.model.Course;
import it.jasonravagli.gym.model.Member;

@RunWith(GUITestRunner.class)
public class SwingGymViewTest extends AssertJSwingJUnitTestCase {

	private AutoCloseable autocloseable;

	private FrameFixture window;

	private SwingGymView swingGymView;

	@Mock
	private DialogManageMember dialogManageMember;

	@Mock
	private DialogManageCourse dialogManageCourse;
	
	@Mock
	private DialogManageCourse dialogManageSubs;

	@Mock
	private GymController controller;

	@Override
	protected void onSetUp() throws Exception {
		autocloseable = MockitoAnnotations.openMocks(this);

		GuiActionRunner.execute(() -> {
			swingGymView = new SwingGymView(controller, dialogManageMember, dialogManageCourse, dialogManageSubs);
			return swingGymView;
		});

		window = new FrameFixture(robot(), swingGymView);
		window.show();
	}

	@Override
	public void onTearDown() throws Exception {
		autocloseable.close();
	}

	@Test
	@GUITest
	public void testControlsInitialState() {
		window.tabbedPane("tabbedPaneMain").requireTabTitles("Members", "Courses").requireSelectedTab(Index.atIndex(0));
		window.label("labelError");

		window.list("listMembers");
		window.button("buttonAddMember").requireEnabled();
		window.button("buttonDeleteMember").requireDisabled();
		window.button("buttonUpdateMember").requireDisabled();
		window.button("buttonRefreshMembers").requireEnabled();

		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		window.list("listCourses");
		window.button(JButtonMatcher.withName("buttonAddCourse")).requireEnabled();
		window.button(JButtonMatcher.withName("buttonDeleteCourse")).requireDisabled();
		window.button(JButtonMatcher.withName("buttonUpdateCourse")).requireDisabled();
		window.button(JButtonMatcher.withName("buttonRefreshCourses")).requireEnabled();
		window.button(JButtonMatcher.withName("buttonManageSubs")).requireDisabled();
	}

	@Test
	@GUITest
	public void testButtonUpdateAndDeleteMemberShouldBeEnabledOnlyWhenAMemberIsSelected() {
		resetControllerMockInvocations();
		JButtonFixture buttonDelete = window.button("buttonDeleteMember");
		JButtonFixture buttonUpdate = window.button("buttonUpdateMember");
		JListFixture listMembers = window.list("listMembers");
		GuiActionRunner.execute(() -> swingGymView.getListModelMembers()
				.addElement(createTestMember("test-name", "test-surname", LocalDate.of(1996, 4, 30))));

		listMembers.selectItem(0);

		buttonDelete.requireEnabled();
		buttonUpdate.requireEnabled();

		listMembers.clearSelection();

		buttonDelete.requireDisabled();
		buttonUpdate.requireDisabled();
	}

	@Test
	@GUITest
	public void testButtonUpdateAndDeleteCourseAndManageSubsShouldBeEnabledOnlyWhenACourseIsSelected() {
		resetControllerMockInvocations();
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		JButtonFixture buttonDelete = window.button("buttonDeleteCourse");
		JButtonFixture buttonUpdate = window.button("buttonUpdateCourse");
		JButtonFixture buttonManageSubs = window.button("buttonManageSubs");
		JListFixture listCourses = window.list("listCourses");
		GuiActionRunner.execute(() -> swingGymView.getListModelCourses().addElement(createTestCourse("test-name")));

		listCourses.selectItem(0);

		buttonDelete.requireEnabled();
		buttonUpdate.requireEnabled();
		buttonManageSubs.requireEnabled();

		listCourses.clearSelection();

		buttonDelete.requireDisabled();
		buttonUpdate.requireDisabled();
		buttonManageSubs.requireDisabled();
	}

	@Test
	@GUITest
	public void testButtonAddMemberWhenClickedShouldShowDialogManageMember() {
		resetControllerMockInvocations();
		window.button("buttonAddMember").click();

		verify(dialogManageMember).show();
	}

	@Test
	@GUITest
	public void testButtonAddMemberWhenDialogClosesAndResultIsOkShouldReloadAllMembers() {
		resetControllerMockInvocations();
		when(dialogManageMember.show()).thenReturn(DialogResult.OK);

		window.button("buttonAddMember").click();

		verify(controller).allMembers();
	}

	@Test
	@GUITest
	public void testButtonAddMemberWhenDialogClosesAndResultIsNotOkShouldDoNothing() {
		resetControllerMockInvocations();
		when(dialogManageMember.show()).thenReturn(DialogResult.CANCEL);

		window.button("buttonAddMember").click();

		verifyNoInteractions(controller);
	}

	@Test
	@GUITest
	public void testButtonUpdateMemberWhenClickedShouldPassSelectedMemberToDialogManageMemberAndShow() {
		resetControllerMockInvocations();
		JListFixture listMembers = window.list("listMembers");
		Member member = createTestMember("test-name", "test-surname", LocalDate.of(1996, 4, 30));
		GuiActionRunner.execute(() -> swingGymView.getListModelMembers().addElement(member));
		listMembers.selectItem(0);

		window.button("buttonUpdateMember").click();

		InOrder inOrder = Mockito.inOrder(dialogManageMember);
		inOrder.verify(dialogManageMember).setMember(member);
		inOrder.verify(dialogManageMember).show();
	}

	@Test
	@GUITest
	public void testButtonUpdateMemberWhenDialogClosesAndResultIsOkShouldReloadAllMembers() {
		resetControllerMockInvocations();
		JListFixture listMembers = window.list("listMembers");
		GuiActionRunner.execute(() -> swingGymView.getListModelMembers()
				.addElement(createTestMember("test-name", "test-surname", LocalDate.of(1996, 4, 30))));
		listMembers.selectItem(0);
		when(dialogManageMember.show()).thenReturn(DialogResult.OK);

		window.button("buttonUpdateMember").click();

		verify(controller).allMembers();
	}

	@Test
	@GUITest
	public void testButtonUpdateMemberWhenDialogClosesAndResultIsNotOkShouldDoNothing() {
		resetControllerMockInvocations();
		JListFixture listMembers = window.list("listMembers");
		GuiActionRunner.execute(() -> swingGymView.getListModelMembers()
				.addElement(createTestMember("test-name", "test-surname", LocalDate.of(1996, 4, 30))));
		listMembers.selectItem(0);
		when(dialogManageMember.show()).thenReturn(DialogResult.CANCEL);

		window.button("buttonUpdateMember").click();

		verifyNoInteractions(controller);
	}

	@Test
	@GUITest
	public void testButtonDeleteMemberWhenClickedShouldCallControllerMethod() {
		resetControllerMockInvocations();
		JListFixture listMembers = window.list("listMembers");
		Member member = createTestMember("test-name", "test-surname", LocalDate.of(1996, 4, 30));
		GuiActionRunner.execute(() -> swingGymView.getListModelMembers().addElement(member));
		listMembers.selectItem(0);

		window.button("buttonDeleteMember").click();

		verify(controller).deleteMember(member);
	}

	@Test
	@GUITest
	public void testButtonRefreshMembersWhenClickedShouldCallControllerMethod() {
		resetControllerMockInvocations();
		window.button("buttonRefreshMembers").click();

		verify(controller).allMembers();
	}

	@Test
	@GUITest
	public void testButtonAddCourseWhenClickedShouldShowDialogManageCourse() {
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		resetControllerMockInvocations();

		window.button("buttonAddCourse").click();

		verify(dialogManageCourse).show();
	}

	@Test
	@GUITest
	public void testButtonAddCourseWhenDialogClosesAndResultIsOkShouldReloadAllCourses() {
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		resetControllerMockInvocations();
		when(dialogManageCourse.show()).thenReturn(DialogResult.OK);

		window.button("buttonAddCourse").click();

		verify(controller).allCourses();
	}

	@Test
	@GUITest
	public void testButtonAddCourseWhenDialogClosesAndResultIsNotOkShouldDoNothing() {
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		resetControllerMockInvocations();
		when(dialogManageCourse.show()).thenReturn(DialogResult.CANCEL);

		window.button("buttonAddCourse").click();

		verifyNoInteractions(controller);
	}

	@Test
	@GUITest
	public void testButtonUpdateCourseWhenClickedShouldPassSelecteCourseToDialogManageCourseAndShow() {
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		resetControllerMockInvocations();
		Course course = createTestCourse("test-name");
		GuiActionRunner.execute(() -> swingGymView.getListModelCourses().addElement(course));
		window.list("listCourses").selectItem(0);

		window.button("buttonUpdateCourse").click();

		InOrder inOrder = Mockito.inOrder(dialogManageCourse);
		inOrder.verify(dialogManageCourse).setCourse(course);
		inOrder.verify(dialogManageCourse).show();
	}

	@Test
	@GUITest
	public void testButtonUpdateCourseWhenDialogClosesAndResultIsOkShouldReloadAllCourses() {
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		resetControllerMockInvocations();
		GuiActionRunner.execute(() -> swingGymView.getListModelCourses().addElement(createTestCourse("test-name")));
		window.list("listCourses").selectItem(0);
		when(dialogManageCourse.show()).thenReturn(DialogResult.OK);

		window.button("buttonUpdateCourse").click();

		verify(controller).allCourses();
	}

	@Test
	@GUITest
	public void testButtonUpdateCourseWhenDialogClosesAndResultIsNotOkShouldDoNothing() {
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		resetControllerMockInvocations();
		GuiActionRunner.execute(() -> swingGymView.getListModelCourses().addElement(createTestCourse("test-name")));
		window.list("listCourses").selectItem(0);
		when(dialogManageCourse.show()).thenReturn(DialogResult.CANCEL);

		window.button("buttonUpdateCourse").click();

		verifyNoInteractions(controller);
	}

	@Test
	@GUITest
	public void testButtonDeleteCourseWhenClickedShouldCallControllerMethod() {
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		resetControllerMockInvocations();
		Course course = createTestCourse("test-name");
		GuiActionRunner.execute(() -> swingGymView.getListModelCourses().addElement(course));
		window.list("listCourses").selectItem(0);

		window.button("buttonDeleteCourse").click();

		verify(controller).deleteCourse(course);
	}

	@Test
	@GUITest
	public void testButtonRefreshCoursesWhenClickedShouldCallControllerMethod() {
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		resetControllerMockInvocations();
		window.button("buttonRefreshCourses").click();

		verify(controller).allCourses();
	}
	
	@Test
	@GUITest
	public void testButtonManageSubsWhenClickedShouldPassSelectedCourseToDialogManageSubsAndShow() {
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		resetControllerMockInvocations();
		Course course = createTestCourse("test-name");
		GuiActionRunner.execute(() -> swingGymView.getListModelCourses().addElement(course));
		window.list("listCourses").selectItem(0);

		window.button("buttonManageSubs").click();

		InOrder inOrder = Mockito.inOrder(dialogManageSubs);
		inOrder.verify(dialogManageSubs).setCourse(course);
		inOrder.verify(dialogManageSubs).show();
	}
	
	@Test
	@GUITest
	public void testButtonManageSubsWhenDialogClosesAndResultIsOkShouldReloadAllCourses() {
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		resetControllerMockInvocations();
		GuiActionRunner.execute(() -> swingGymView.getListModelCourses().addElement(createTestCourse("test-name")));
		window.list("listCourses").selectItem(0);
		when(dialogManageSubs.show()).thenReturn(DialogResult.OK);

		window.button("buttonManageSubs").click();

		verify(controller).allCourses();
	}

	@Test
	@GUITest
	public void testButtonManageSubsWhenDialogClosesAndResultIsNotOkShouldDoNothing() {
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		resetControllerMockInvocations();
		GuiActionRunner.execute(() -> swingGymView.getListModelCourses().addElement(createTestCourse("test-name")));
		window.list("listCourses").selectItem(0);
		when(dialogManageSubs.show()).thenReturn(DialogResult.CANCEL);

		window.button("buttonManageSubs").click();

		verifyNoInteractions(controller);
	}
	
	@Test
	public void testWhenCoursesTabIsSelectedShouldLoadCourses() {
		resetControllerMockInvocations();
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		
		verify(controller).allCourses();
	}
	
	@Test
	public void testWhenMembersTabIsSelectedShouldLoadMembers() {
		resetControllerMockInvocations();
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		window.tabbedPane("tabbedPaneMain").selectTab("Members");
		
		verify(controller).allMembers();
	}

	/*
	 * Only for documentation
	 */
	@Test
	public void testWhenViewIsShownShouldLoadMembers() {
		verify(controller).allMembers();
	}

	@Test
	public void testShowMembersShouldClearTheListAndAddMembers() {
		Member oldMember = createTestMember("name-old", "surname-old", LocalDate.of(1995, 4, 28));
		GuiActionRunner.execute(() -> swingGymView.getListModelMembers().addElement(oldMember));

		Member member1 = createTestMember("name-1", "surname-1", LocalDate.of(1996, 10, 31));
		Member member2 = createTestMember("name-2", "surname-2", LocalDate.of(1996, 4, 30));
		swingGymView.showMembers(Arrays.asList(member1, member2));

		assertThat(window.list("listMembers").contents()).containsExactly(member1.toString(), member2.toString());
	}
	
	@Test
	public void testShowMembersShouldClearTheErrorLabel() {
		JLabelFixture errorLabel = window.label("labelError");
		GuiActionRunner.execute(() -> errorLabel.target().setText("Some text"));

		swingGymView.showMembers(Collections.emptyList());

		errorLabel.requireText(" ");
	}

	@Test
	public void testMemberAddedShouldThrow() {
		assertThatThrownBy(
				() -> swingGymView.memberAdded(createTestMember("name-1", "surname-1", LocalDate.of(1996, 10, 31))))
						.isInstanceOf(UnsupportedOperationException.class).hasMessage("Operation not supported");
	}

	@Test
	public void testMemberUpdatedShouldThrow() {
		assertThatThrownBy(
				() -> swingGymView.memberUpdated(createTestMember("name-1", "surname-1", LocalDate.of(1996, 10, 31))))
						.isInstanceOf(UnsupportedOperationException.class).hasMessage("Operation not supported");
	}

	@Test
	@GUITest
	public void testMemberDeletedShouldRemoveMemberFromTheList() {
		Member member1 = createTestMember("name-1", "surname-1", LocalDate.of(1996, 10, 31));
		Member member2 = createTestMember("name-2", "surname-2", LocalDate.of(1996, 4, 30));
		GuiActionRunner.execute(() -> Arrays.asList(member1, member2).forEach(swingGymView.getListModelMembers()::addElement));

		swingGymView.memberDeleted(member2);

		assertThat(window.list("listMembers").contents()).containsExactly(member1.toString());
	}
	
	@Test
	@GUITest
	public void testMemberDeletedShouldClearTheErrorLabel() {
		JLabelFixture errorLabel = window.label("labelError");
		GuiActionRunner.execute(() -> errorLabel.target().setText("Some text"));
		Member member1 = createTestMember("name-1", "surname-1", LocalDate.of(1996, 10, 31));
		Member member2 = createTestMember("name-2", "surname-2", LocalDate.of(1996, 4, 30));
		GuiActionRunner.execute(() -> Arrays.asList(member1, member2).forEach(swingGymView.getListModelMembers()::addElement));

		swingGymView.memberDeleted(member2);

		errorLabel.requireText(" ");
	}

	@Test
	@GUITest
	public void testShowCoursesShouldClearTheListAndAddCourses() {
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		Course oldCourse = createTestCourse("name-old");
		GuiActionRunner.execute(() -> swingGymView.getListModelCourses().addElement(oldCourse));

		Course course1 = createTestCourse("name-1");
		Course course2 = createTestCourse("name-2");
		swingGymView.showCourses(Arrays.asList(course1, course2));

		assertThat(window.list("listCourses").contents()).containsExactly(course1.toString(), course2.toString());
	}
	
	@Test
	@GUITest
	public void testShowCoursesShouldClearTheErrorLabel() {
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		JLabelFixture errorLabel = window.label("labelError");
		GuiActionRunner.execute(() -> errorLabel.target().setText("Some text"));
		
		swingGymView.showCourses(Collections.emptyList());

		errorLabel.requireText(" ");
	}

	@Test
	public void testCourseAddedShouldThrow() {
		assertThatThrownBy(
				() -> swingGymView.courseAdded(createTestCourse("name-1")))
						.isInstanceOf(UnsupportedOperationException.class).hasMessage("Operation not supported");
	}

	@Test
	public void tesCourseUpdatedShouldThrow() {
		assertThatThrownBy(
				() -> swingGymView.courseUpdated(createTestCourse("name-1")))
						.isInstanceOf(UnsupportedOperationException.class).hasMessage("Operation not supported");
	}

	@Test
	@GUITest
	public void testCourseDeletedShouldRemoveCourseFromTheList() {
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		Course course1 = createTestCourse("name-1");
		Course course2 = createTestCourse("name-2");
		GuiActionRunner.execute(() -> Arrays.asList(course1, course2).forEach(swingGymView.getListModelCourses()::addElement));

		swingGymView.courseDeleted(course2);

		assertThat(window.list("listCourses").contents()).containsExactly(course1.toString());
	}
	
	@Test
	@GUITest
	public void testCourseDeletedShouldClearTheErrorLabel() {
		window.tabbedPane("tabbedPaneMain").selectTab("Courses");
		JLabelFixture errorLabel = window.label("labelError");
		GuiActionRunner.execute(() -> errorLabel.target().setText("Some text"));
		Course course1 = createTestCourse("name-1");
		Course course2 = createTestCourse("name-2");
		GuiActionRunner.execute(() -> Arrays.asList(course1, course2).forEach(swingGymView.getListModelCourses()::addElement));

		swingGymView.courseDeleted(course2);

		errorLabel.requireText(" ");
	}
	
	@Test
	@GUITest
	public void testShowErrorShouldShowTheErrorInTheLabel() {
		String errorMessage = "An error occurred";
		swingGymView.showError(errorMessage);
		
		window.label("labelError").requireText(errorMessage);
	}

	private Member createTestMember(String name, String surname, LocalDate dateOfBirth) {
		Member member = new Member();
		member.setId(UUID.randomUUID());
		member.setName(name);
		member.setSurname(surname);
		member.setDateOfBirth(dateOfBirth);

		return member;
	}

	private Course createTestCourse(String name) {
		Course course = new Course();
		course.setId(UUID.randomUUID());
		course.setName(name);
		course.setSubscribers(Collections.emptySet());

		return course;
	}

	/*
	 * Useful to clear the controller mock from invocations done by automatic events
	 * like the window opening
	 */
	private void resetControllerMockInvocations() {
		Mockito.clearInvocations(controller);
	}

}