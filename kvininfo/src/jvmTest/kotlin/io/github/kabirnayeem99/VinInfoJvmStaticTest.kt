package io.github.kabirnayeem99

import io.github.kabirnayeem99.viminfo.VinInfo
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Exercises the compiler-generated @JvmStatic bridge methods in the outer VinInfo class.
 *
 * Kotlin always routes calls through the companion object, so the static bridges in the outer
 * class only get hit when called via Java reflection (or from Java code). These tests exist solely
 * to achieve JaCoCo line coverage on those two synthesised bridge methods.
 */
class VinInfoJvmStaticTest {

    @Test
    fun `JvmStatic random bridge is callable via reflection`() {
        val method = VinInfo::class.java.getMethod("random")
        val result = method.invoke(null) as VinInfo
        assertNotNull(result)
        result.close()
    }

    @Test
    fun `JvmStatic fromNumber bridge is callable via reflection`() {
        // Result<VinInfo> is a value class; the JVM bridge name is mangled with a hash suffix.
        val method = VinInfo::class.java.getDeclaredMethod("fromNumber-IoAF18A", String::class.java)
        method.isAccessible = true
        val boxed = method.invoke(null, "1HGBH41JXMN109186")
        assertTrue(boxed != null)
    }
}
