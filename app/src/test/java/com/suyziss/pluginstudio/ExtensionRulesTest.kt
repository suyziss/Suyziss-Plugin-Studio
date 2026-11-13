package com.suyziss.pluginstudio

import com.suyziss.pluginstudio.data.ExtensionRules
import org.junit.Assert.*
import org.junit.Test

class ExtensionRulesTest {
    @Test fun customExtensionsAreAccepted() {
        assertTrue(ExtensionRules.isValid(".plugin"))
        assertTrue(ExtensionRules.isValid(".myscript"))
        assertFalse(ExtensionRules.isValid("..bad"))
    }
    @Test fun fileNameIsBuiltFromProfile() {
        assertEquals("BarcodeLang.plugin", ExtensionRules.fileName("BarcodeLang", ".plugin"))
    }
}
