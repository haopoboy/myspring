package io.github.haopoboy.myspring.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.data.domain.PageRequest

class PagesUnitTests {

    @Test
    fun scroll() {
        val list = listOf("1", "2", "3")
        Pages.scroll { Pages.paging(list, it) }
                .let {
                    assertThat(it.pageable(2)).isNotEqualTo(it)
                    assertThat(it.pageable(0, 2)).isNotEqualTo(it)
                    assertThat(it.pageable(PageRequest.of(0, 2))).isNotEqualTo(it)
                    it.forEach { element ->
                        assertThat(list).contains(element)
                    }
                }
    }

    @Test
    fun pageableBySize() {
        val list = listOf("1", "2", "3")
        Pages.scroll { Pages.paging(list, it) }
                .pageable(1)
                .forEachIndexed { index, element ->
                    assertThat(list[index]).isEqualTo(element)
                }
    }

    @Test
    fun pageableByPageAndSize() {
        val list = listOf("1", "2", "3")
        Pages.scroll { Pages.paging(list, it) }
                .pageable(0, 1)
                .forEachIndexed { index, element ->
                    assertThat(list[index]).isEqualTo(element)
                }
    }
}

