package io.github.haopoboy.myspring.util

import me.tongfei.progressbar.ProgressBar
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

class Pages {

    companion object {

        /**
         * Scroll
         */
        fun <T> scroll(scroll: (Pageable) -> Page<T>): Iterator<T> {
            return Iterator(scroll, PageRequest.of(0, 100))
        }

        /**
         * Paging list by pageable
         */
        fun <T> paging(list: List<T>, pageable: Pageable): Page<T> {
            return PageImpl(list.subList(pageable.pageNumber, pageable.offset.toInt() + 1), pageable, list.size.toLong())
        }
    }

    class Iterator<T>(
            private val scroll: (Pageable) -> Page<T>,
            private val pageable: Pageable = PageRequest.of(0, 100)) {

        fun pageable(pageable: Pageable): Iterator<T> {
            return Iterator(scroll, pageable)
        }

        fun pageable(page: Int = 0, size: Int = 1000): Iterator<T> {
            return pageable(PageRequest.of(page, size))
        }

        fun pageable(size: Int = 1000): Iterator<T> {
            return pageable(PageRequest.of(0, size))
        }

        fun forEach(action: (T) -> Unit) {
            var current = scroll(pageable)
            ProgressBar(javaClass.simpleName, current.totalElements).use { progressBar ->
                do {
                    current.content.forEach(action)
                    val step = current.number * current.size + current.content.size
                    progressBar.stepTo(step.toLong())
                    val pageable = current.nextPageable()
                    if (pageable.isPaged) {
                        current = scroll(pageable)
                    }
                } while (pageable.isPaged)
            }
        }

        fun forEachIndexed(action: (index: Int, T) -> Unit) {
            var current = scroll(pageable)
            ProgressBar(javaClass.simpleName, current.totalElements).use { progressBar ->
                do {
                    current.content.forEachIndexed { index, element ->
                        action((index + current.pageable.offset).toInt(), element)
                    }
                    val step = current.number * current.size + current.content.size
                    progressBar.stepTo(step.toLong())
                    val pageable = current.nextPageable()
                    if (pageable.isPaged) {
                        current = scroll(pageable)
                    }
                } while (pageable.isPaged)
            }
        }
    }
}