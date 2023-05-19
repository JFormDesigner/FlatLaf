/*
 * Copyright 2020 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * @author Karl Tauber
 */
module com.formdev.flatlaf.swingx {
	requires java.desktop;
	requires swingx.all;
	requires com.formdev.flatlaf;

	exports com.formdev.flatlaf.swingx;
	exports com.formdev.flatlaf.swingx.icons;
	exports com.formdev.flatlaf.swingx.ui;

	// this allows com.formdev.flatlaf.FlatDefaultsAddon to read .properties files
	opens com.formdev.flatlaf.swingx
		to com.formdev.flatlaf;

	provides com.formdev.flatlaf.FlatDefaultsAddon
		with com.formdev.flatlaf.swingx.FlatSwingXDefaultsAddon;
	provides org.jdesktop.swingx.plaf.LookAndFeelAddons
		with com.formdev.flatlaf.swingx.FlatLookAndFeelAddons;
}
