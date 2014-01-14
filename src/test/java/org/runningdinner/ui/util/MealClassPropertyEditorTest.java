package org.runningdinner.ui.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.runningdinner.core.MealClass;

public class MealClassPropertyEditorTest {

	private MealClassPropertyEditor editor;
	Set<MealClass> mealClasses = new HashSet<MealClass>();

	@Before
	public void setUp() {
		editor = new MealClassPropertyEditor();

		this.mealClasses.clear();
		this.mealClasses.add(MealClass.APPETIZER);
		this.mealClasses.add(MealClass.MAINCOURSE);
		this.mealClasses.add(MealClass.DESSERT);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConvertFromJson() {
		String json = "[{\"label\" : \"Vorspeise\"},{\"label\":\"Nachspeise\"}, {\"label\":\"Hauptgericht\"}]";
		editor.setAsText(json);
		Set<MealClass> convertedMealClasses = (Set<MealClass>)editor.getValue();
		assertEquals(3, convertedMealClasses.size());
		assertEquals(true, convertedMealClasses.contains(MealClass.APPETIZER));
		assertEquals(true, convertedMealClasses.contains(MealClass.MAINCOURSE));
		assertEquals(true, convertedMealClasses.contains(MealClass.DESSERT));
	}

	@Test
	public void testConvertToJson() {
		editor.setValue(mealClasses);
		String json = editor.getAsText();
		assertTrue(json.length() > 0);
		assertEquals(
				"[{\"label\":\"Vorspeise\",\"time\":null},{\"label\":\"Nachspeise\",\"time\":null},{\"label\":\"Hauptgericht\",\"time\":null}]",
				json);
		System.out.println(json);
	}
}
