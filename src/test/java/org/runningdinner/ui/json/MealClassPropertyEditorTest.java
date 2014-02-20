package org.runningdinner.ui.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.runningdinner.core.MealClass;
import org.runningdinner.ui.util.MealClassPropertyEditor;

public class MealClassPropertyEditorTest {

	private MealClassPropertyEditor editor;
	Set<MealClass> mealClasses = new HashSet<MealClass>();

	@Before
	public void setUp() {
		editor = new MealClassPropertyEditor();

		this.mealClasses.clear();
		this.mealClasses.add(MealClass.APPETIZER());
		this.mealClasses.add(MealClass.MAINCOURSE());
		this.mealClasses.add(MealClass.DESSERT());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConvertFromJson() {
		String json = "[{\"label\" : \"Vorspeise\"},{\"label\":\"Nachspeise\"}, {\"label\":\"Hauptgericht\"}]";
		editor.setAsText(json);
		List<MealClass> convertedMealClasses = (List<MealClass>)editor.getValue();
		assertEquals(3, convertedMealClasses.size());
		assertEquals(true, convertedMealClasses.contains(MealClass.APPETIZER()));
		assertEquals(true, convertedMealClasses.contains(MealClass.MAINCOURSE()));
		assertEquals(true, convertedMealClasses.contains(MealClass.DESSERT()));
	}

	@Test
	public void testConvertToJson() {
		editor.setValue(new ArrayList<MealClass>(mealClasses));
		String json = editor.getAsText();
		assertTrue(json.length() > 0);
		System.out.println(json);
		assertEquals(
				"[{\"label\":\"Vorspeise\",\"time\":null},{\"label\":\"Nachspeise\",\"time\":null},{\"label\":\"Hauptgericht\",\"time\":null}]",
				json);

	}
}
