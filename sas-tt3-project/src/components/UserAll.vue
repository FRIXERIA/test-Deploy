<script setup>
import { ref, onMounted, computed, inject, onUpdated, onBeforeUpdate, VueElement, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAnnounce, useMode } from '../stores/announcement'
import { fetchCategory, fetchPage, fetchId } from '../code/get.js';
const myAnnounce = useAnnounce()
let category = ref({})
let pageActive = ref([])
let pageClose = ref([])
let selectCategory = ref()
let cate = ref()
let numButton = ref(10)
let displayedPagesActive
let displayedPagesClose
let myMode = useMode()
let showButtonActive = ref(true)
let showButtonClose = ref(true)
let countActive = ref(0)
let countClose = ref(0)
let perPage = 5
const payloadObject = ref()
const isAdmin = ref(false)

onMounted(async () => {
  if (jwtToken) {
    const base64Payload = jwtToken.split(".")[1];
    const decodePayload = atob(base64Payload);
    payloadObject.value = JSON.parse(decodePayload);
    // console.log(payloadObject.value);
    if (payloadObject.value.role == 'announcer') {
      console.log('announcer');
    }
    if (payloadObject.value.role == 'admin') {
      console.log('admin');
      isAdmin.value = true
    }
  }
  // myMode.mode='active'
  pageActive.value = await fetchPage(0, "active", null, 5)
  pageClose.value = await fetchPage(0, "close", null, 5)
  cate.value = "ทั้งหมด"

  category.value = await fetchCategory()


  if (pageActive.value.content.length === 0) {
    error.value = 'No Announcements'
    showError.value = true
  }
  if (pageClose.value.content.length === 0) {
    error.value = 'No Announcements'
    showError.value = true
  }

  if (pageActive.value.content.length !== 0) {
    showError.value = false

  }
  if (pageActive.value.totalElements < 5) {
    showButtonActive.value = false
  }
  if (pageActive.value.totalElements >= 5) {
    showButtonActive.value = true
  }
  if (pageClose.value.content.length !== 0) {
    showError.value = false
  }
  if (pageClose.value.totalElements < 5) {
    showButtonClose.value = false
  }
  if (pageClose.value.totalElements >= 5) {
    showButtonClose.value = true
  }
  pageClose.value.content = pageClose.value.content.map(data => {

    if (data.closeDate !== null) {
      data.closeDate = convertDate(data.closeDate)
    }

    return data
  })
  if (myMode.mode === "close") {
    showActive.value = false
    showClosed.value = true
  }
  if (myMode.mode === "active") {
    showClosed.value = false
    showActive.value = true

  }
})

onBeforeUpdate(async () => {
  displayedPagesActive = computed(() => {
    let start = Math.max(1, pageActive.value.page + 1 - Math.floor(numButton.value) + 1)
    let end = Math.min(pageActive.value.totalPages, start + numButton.value - 1)
    let pages = []
    for (let i = start; i <= end; i++) {
      pages.push(i)
    }

    return pages
  })


  displayedPagesClose = computed(() => {
    let start = Math.max(1, pageClose.value.page + 1 - Math.floor(numButton.value) + 1)
    let end = Math.min(pageClose.value.totalPages, start + numButton.value - 1)
    let pages = []
    if (start === 1) {
      end = Math.min(pageClose.value.totalPages, start + numButton.value - 1);
    } else if (end === pageClose.value.totalPages) {
      start = Math.max(1, end - numButton.value + 1);
    }
    for (let i = start; i <= end; i++) {
      pages.push(i)
    }

    return pages
  })
  if (pageClose.value.totalElements < 5) {
    // console.log('a')
    showButtonClose.value = false
  }
  if (pageClose.value.totalElements >= 5) {
    // console.log('a')
    showButtonClose.value = true
  }
  if (pageActive.value.totalElements < 5) {
    // console.log('a')
    showButtonActive.value = false
  }
  if (pageActive.value.totalElements >= 5) {
    // console.log('a')
    showButtonActive.value = true
  }
})
async function showCategory(event) {
  // myMode.category='ทั้งหมด'
  if (event.target.id === "active") {
    myMode.setMode('active')
    if (cate.value === "ทั้งหมด") {
      myMode.setCategory('ทั้งหมด')
      myMode.setCategoryId(null)
      pageActive.value = await fetchPage(0, "active", myMode.categoryId, 5)
      if (pageActive.value.content.length === 0) {
        error.value = 'No Announcements'
        showError.value = true
        showButtonActive.value = false
      }
      if (pageActive.value.content.length !== 0) {
        showError.value = false
        showButtonActive.value = true
      }
      if (pageActive.value.totalElements < 5) {
        showButtonActive.value = false
      }
      pageClose.value = await fetchPage(0, "close", myMode.categoryId, 5)
      pageClose.value.content = pageClose.value.content.map(data => {

        if (data.closeDate !== null) {
          data.closeDate = convertDate(data.closeDate)
        }

        return data
      })

      // console.log(pageActive.value)
    }
    if (cate.value === "ทั่วไป") {
      myMode.setCategory('ทั่วไป')
      myMode.setCategoryId(1)
      pageActive.value = await fetchPage(0, "active", myMode.categoryId, 5)
      if (pageActive.value.content.length === 0) {
        error.value = 'No Announcements'
        showError.value = true
        showButtonActive.value = false
      }
      if (pageActive.value.content.length !== 0) {
        showError.value = false
        showButtonActive.value = true
      }
      if (pageActive.value.totalElements < 5) {
        showButtonActive.value = false
      }
      pageClose.value = await fetchPage(0, "close", myMode.categoryId, 5)
      pageClose.value.content = pageClose.value.content.map(data => {

        if (data.closeDate !== null) {
          data.closeDate = convertDate(data.closeDate)
        }

        return data
      })

    }
    if (cate.value === "ทุนการศึกษา") {
      myMode.setCategory('ทุนการศึกษา')
      myMode.setCategoryId(2)
      pageActive.value = await fetchPage(0, "active", myMode.categoryId, 5)
      if (pageActive.value.content.length === 0) {
        error.value = 'No Announcements'
        showError.value = true
        showButtonActive.value = false

      }
      if (pageActive.value.content.length !== 0) {
        showError.value = false
        showButtonActive.value = true
      }
      if (pageActive.value.totalElements < 5) {
        showButtonActive.value = false
      }
      pageClose.value = await fetchPage(0, "close", myMode.categoryId, 5)
      pageClose.value.content = pageClose.value.content.map(data => {

        if (data.closeDate !== null) {
          data.closeDate = convertDate(data.closeDate)
        }

        return data
      })

    }

    if (cate.value === "หางาน") {
      myMode.setCategory('หางาน')
      myMode.setCategoryId(3)
      pageActive.value = await fetchPage(0, "active", myMode.categoryId, 5)
      if (pageActive.value.content.length === 0) {
        error.value = 'No Announcements'
        showError.value = true
        showButtonActive.value = false
      }
      if (pageActive.value.content.length !== 0) {
        showError.value = false
        showButtonActive.value = true
      }
      if (pageActive.value.totalElements < 5) {
        showButtonActive.value = false
      }
      pageClose.value = await fetchPage(0, "close", myMode.categoryId, 5)
      pageClose.value.content = pageClose.value.content.map(data => {

        if (data.closeDate !== null) {
          data.closeDate = convertDate(data.closeDate)
        }

        return data
      })

    }
    if (cate.value === "ฝึกงาน") {
      myMode.setCategory('ฝึกงาน')
      myMode.setCategoryId(4)
      pageActive.value = await fetchPage(0, "active", myMode.categoryId, 5)
      if (pageActive.value.content.length === 0) {
        error.value = 'No Announcements'
        showError.value = true
        showButtonActive.value = false
      }
      if (pageActive.value.content.length !== 0) {
        showError.value = false
        showButtonActive.value = true
      }
      if (pageActive.value.totalElements < 5) {
        showButtonActive.value = false
      }
      pageClose.value = await fetchPage(0, "close", myMode.categoryId, 5)
      pageClose.value.content = pageClose.value.content.map(data => {

        if (data.closeDate !== null) {
          data.closeDate = convertDate(data.closeDate)
        }

        return data
      })

    }
  }


  if (event.target.id === "close") {
    myMode.setMode('close')
    if (cate.value === "ทั้งหมด") {
      myMode.setCategory('ทั้งหมด')
      myMode.setCategoryId(null)
      pageClose.value = await fetchPage(0, "close", myMode.categoryId, 5)
      pageClose.value.content = pageClose.value.content.map(data => {

        if (data.closeDate !== null) {
          data.closeDate = convertDate(data.closeDate)
        }

        return data
      })
      if (pageClose.value.content.length === 0) {
        error.value = 'No Announcements'
        showError.value = true
        showButtonClose.value = false
      }
      if (pageClose.value.content.length !== 0) {
        showError.value = false
        showButtonClose.value = true
      }
      if (pageClose.value.totalElements < 5) {
        showButtonClose.value = false
      }
      pageActive.value = await fetchPage(0, "active", myMode.categoryId, 5)

      // console.log(pageClose.value)
    }

    if (cate.value === "ทั่วไป") {
      myMode.setCategory('ทั่วไป')
      myMode.setCategoryId(1)
      pageClose.value = await fetchPage(0, "close", myMode.categoryId, 5)
      pageClose.value.content = pageClose.value.content.map(data => {

        if (data.closeDate !== null) {
          data.closeDate = convertDate(data.closeDate)
        }

        return data
      })
      if (pageClose.value.content.length === 0) {
        error.value = 'No Announcements'
        showError.value = true
        showButtonClose.value = false
      }
      if (pageClose.value.content.length !== 0) {
        showError.value = false
        showButtonClose.value = true
      }
      if (pageClose.value.totalElements < 5) {
        showButtonClose.value = false
      }
      pageActive.value = await fetchPage(0, "active", myMode.categoryId, 5)

      // console.log(pageClose.value)
    }

    if (cate.value === "ทุนการศึกษา") {
      myMode.setCategory('ทุนการศึกษา')
      myMode.setCategoryId(2)
      pageClose.value = await fetchPage(0, "close", myMode.categoryId, 5)
      pageClose.value.content = pageClose.value.content.map(data => {

        if (data.closeDate !== null) {
          data.closeDate = convertDate(data.closeDate)
        }

        return data
      })
      if (pageClose.value.content.length === 0) {
        error.value = 'No Announcements'
        showError.value = true
        showButtonClose.value = false
      }
      if (pageClose.value.content.length !== 0) {
        showError.value = false
        showButtonClose.value = true
      }

      if (pageClose.value.totalElements < 5) {
        showButtonClose.value = false
      }
      pageActive.value = await fetchPage(0, "active", myMode.categoryId, 5)
      // console.log(pageClose.value)
    }

    if (cate.value === "หางาน") {
      myMode.setCategory('หางาน')
      myMode.setCategoryId(3)
      pageClose.value = await fetchPage(0, "close", myMode.categoryId, 5)
      pageClose.value.content = pageClose.value.content.map(data => {

        if (data.closeDate !== null) {
          data.closeDate = convertDate(data.closeDate)
        }

        return data
      })
      if (pageClose.value.content.length === 0) {
        error.value = 'No Announcements'
        showError.value = true
        showButtonClose.value = false
      }
      if (pageClose.value.content.length !== 0) {
        showError.value = false
        showButtonClose.value = true
      }
      if (pageClose.value.totalElements < 5) {
        showButtonClose.value = false
      }
      pageActive.value = await fetchPage(0, "active", myMode.categoryId, 5)
      // console.log(pageClose.value)
    }

    if (cate.value === "ฝึกงาน") {
      myMode.setCategory('ฝึกงาน')
      myMode.setCategoryId(4)
      pageClose.value = await fetchPage(0, "close", myMode.categoryId, 5)
      pageClose.value.content = pageClose.value.content.map(data => {

        if (data.closeDate !== null) {
          data.closeDate = convertDate(data.closeDate)
        }

        return data
      })
      if (pageClose.value.content.length === 0) {
        error.value = 'No Announcements'
        showError.value = true
        showButtonClose.value = false
      }
      if (pageClose.value.content.length !== 0) {
        showError.value = false
        showButtonClose.value = true
      }
      if (pageClose.value.totalElements < 5) {
        showButtonClose.value = false
      }
      pageActive.value = await fetchPage(0, "active", myMode.categoryId, 5)
      // console.log(pageClose.value)
    }
  }
}

async function pageSwitch(number, event) {
  // console.log(number)
  //active
  if (event.target.id === "active") {
    if (cate.value !== null) {
      if (cate.value === "ทั้งหมด") {
        pageActive.value = await fetchPage(number - 1, "active", null, 5)
        // console.log("a")
      }
      if (cate.value === "ทั่วไป") {
        pageActive.value = await fetchPage(number - 1, "active", 1, 5)
        // console.log("b")
      }
      if (cate.value === "ทุนการศึกษา") {
        pageActive.value = await fetchPage(number - 1, "active", 2, 5)
      }
      if (cate.value === "หางาน") {
        pageActive.value = await fetchPage(number - 1, "active", 3, 5)
      }
      if (cate.value === "ฝึกงาน") {
        pageActive.value = await fetchPage(number - 1, "active", 4, 5)
      }

    }
    // console.log(cate.value)
    if (cate.value === undefined) {
      pageActive.value = await fetchPage(number - 1, "active", null, 5)

    }


  }
  // nextActive
  if (event.target.id === "nextActive") {
    // console.log(event.target.id)
    if (cate.value !== null) {
      if (cate.value === "ทั้งหมด") {
        pageActive.value = await fetchPage(number, "active", null, 5)
        // console.log("a")
      }
      if (cate.value === "ทั่วไป") {
        pageActive.value = await fetchPage(number, "active", 1, 5)
        // console.log("b")
      }
      if (cate.value === "ทุนการศึกษา") {
        pageActive.value = await fetchPage(number, "active", 2, 5)
      }
      if (cate.value === "หางาน") {
        pageActive.value = await fetchPage(number, "active", 3, 5)
      }
      if (cate.value === "ฝึกงาน") {
        pageActive.value = await fetchPage(number, "active", 4, 5)
      }

    }
    // console.log(cate.value)
    if (cate.value === undefined) {
      pageActive.value = await fetchPage(number, "active", null, 5)
      // console.log(pageActive.value)
    }
  }


  //prevActive
  if (event.target.id === "prevActive") {
    // console.log(event.target.id)
    if (cate.value !== null) {
      if (cate.value === "ทั้งหมด") {
        pageActive.value = await fetchPage(number, "active", null, 5)
        // console.log("a")
      }
      if (cate.value === "ทั่วไป") {
        pageActive.value = await fetchPage(number, "active", 1, 5)
        // console.log("b")
      }
      if (cate.value === "ทุนการศึกษา") {
        pageActive.value = await fetchPage(number, "active", 2, 5)
      }
      if (cate.value === "หางาน") {
        pageActive.value = await fetchPage(number, "active", 3, 5)
      }
      if (cate.value === "ฝึกงาน") {
        pageActive.value = await fetchPage(number, "active", 4, 5)
      }

    }
    // console.log(cate.value)
    if (cate.value === undefined) {
      pageActive.value = await fetchPage(number, "active", null, 5)
      // console.log(pageActive.value)
    }
  }

  if (event.target.id === "close") {
    if (cate.value !== null) {
      if (cate.value === "ทั้งหมด") {
        pageClose.value = await fetchPage(number - 1, "close", null, 5)
        pageClose.value.content = pageClose.value.content.map(data => {

          if (data.closeDate !== null) {
            data.closeDate = convertDate(data.closeDate)
          }

          return data
        })
      }
      if (cate.value === "ทั่วไป") {
        pageClose.value = await fetchPage(number - 1, "close", 1, 5)
        pageClose.value.content = pageClose.value.content.map(data => {

          if (data.closeDate !== null) {
            data.closeDate = convertDate(data.closeDate)
          }

          return data
        })
      }
      if (cate.value === "ทุนการศึกษา") {
        pageClose.value = await fetchPage(number - 1, "close", 2, 5)
        pageClose.value.content = pageClose.value.content.map(data => {
          if (data.closeDate !== null) {
            data.closeDate = convertDate(data.closeDate)
          }

          return data
        })
      }
      if (cate.value === "หางาน") {
        pageClose.value = await fetchPage(number - 1, "close", 3, 5)
        pageClose.value.content = pageClose.value.content.map(data => {

          if (data.closeDate !== null) {
            data.closeDate = convertDate(data.closeDate)
          }

          return data
        })
      }
      if (cate.value === "ฝึกงาน") {
        pageClose.value = await fetchPage(number - 1, "close", 4, 5)
        pageClose.value.content = pageClose.value.content.map(data => {

          if (data.closeDate !== null) {
            data.closeDate = convertDate(data.closeDate)
          }
          return data
        })
      }

    }

    if (cate.value === undefined) {
      pageClose.value = await fetchPage(number - 1, "close", null, 5)
      pageClose.value.content = pageClose.value.content.map(data => {

        if (data.closeDate !== null) {
          data.closeDate = convertDate(data.closeDate)
        }

        return data
      })
    }
  }


  if (event.target.id === "nextClose") {
    if (cate.value !== null) {
      if (cate.value === "ทั้งหมด") {
        pageClose.value = await fetchPage(number, "close", null, 5)
        pageClose.value.content = pageClose.value.content.map(data => {

          if (data.closeDate !== null) {
            data.closeDate = convertDate(data.closeDate)
          }

          return data
        })
      }
      if (cate.value === "ทั่วไป") {
        pageClose.value = await fetchPage(number, "close", 1, 5)
        pageClose.value.content = pageClose.value.content.map(data => {

          if (data.closeDate !== null) {
            data.closeDate = convertDate(data.closeDate)
          }

          return data
        })
      }
      if (cate.value === "ทุนการศึกษา") {
        pageClose.value = await fetchPage(number, "close", 2, 5)
        pageClose.value.content = pageClose.value.content.map(data => {

          if (data.closeDate !== null) {
            data.closeDate = convertDate(data.closeDate)
          }

          return data
        })
      }
      if (cate.value === "หางาน") {
        pageClose.value = await fetchPage(number, "close", 3, 5)
        pageClose.value.content = pageClose.value.content.map(data => {

          if (data.closeDate !== null) {
            data.closeDate = convertDate(data.closeDate)
          }

          return data
        })
      }
      if (cate.value === "ฝึกงาน") {
        pageClose.value = await fetchPage(number, "close", 4, 5)
        pageClose.value.content = pageClose.value.content.map(data => {

          if (data.closeDate !== null) {
            data.closeDate = convertDate(data.closeDate)
          }

          return data
        })
      }

    }

    if (cate.value === undefined) {
      pageClose.value = await fetchPage(number, "close", null, 5)
      pageClose.value.content = pageClose.value.content.map(data => {

        if (data.closeDate !== null) {
          data.closeDate = convertDate(data.closeDate)
        }

        return data
      })
    }
  }


  if (event.target.id === "prevClose") {
    if (cate.value !== null) {
      if (cate.value === "ทั้งหมด") {
        pageClose.value = await fetchPage(number, "close", null, 5)
        pageClose.value.content = pageClose.value.content.map(data => {

          if (data.closeDate !== null) {
            data.closeDate = convertDate(data.closeDate)
          }

          return data
        })
      }
      if (cate.value === "ทั่วไป") {
        pageClose.value = await fetchPage(number, "close", 1, 5)
        pageClose.value.content = pageClose.value.content.map(data => {

          if (data.closeDate !== null) {
            data.closeDate = convertDate(data.closeDate)
          }

          return data
        })
      }
      if (cate.value === "ทุนการศึกษา") {
        pageClose.value = await fetchPage(number, "close", 2, 5)
        pageClose.value.content = pageClose.value.content.map(data => {

          if (data.closeDate !== null) {
            data.closeDate = convertDate(data.closeDate)
          }

          return data
        })
      }
      if (cate.value === "หางาน") {
        pageClose.value = await fetchPage(number, "close", 3, 5)
        pageClose.value.content = pageClose.value.content.map(data => {

          if (data.closeDate !== null) {
            data.closeDate = convertDate(data.closeDate)
          }

          return data
        })
      }
      if (cate.value === "ฝึกงาน") {
        pageClose.value = await fetchPage(number, "close", 4, 5)
        pageClose.value.content = pageClose.value.content.map(data => {

          if (data.closeDate !== null) {
            data.closeDate = convertDate(data.closeDate)
          }

          return data
        })
      }

    }

    if (cate.value === undefined) {
      pageClose.value = await fetchPage(number, "close", null, 5)
      pageClose.value.content = pageClose.value.content.map(data => {

        if (data.closeDate !== null) {
          data.closeDate = convertDate(data.closeDate)
        }

        return data
      })
    }
  }

}

let showError = ref(true)
let showDate = ref(true)
let NoDate = ref()
const router = useRouter()
const date = ref({})
let error = ref('')
let newDate = ref()
const jwtToken = localStorage.getItem('jwtToken')
const refreshToken = localStorage.getItem('refreshToken')

const options = ref({
  year: 'numeric',
  month: 'short',
  day: 'numeric',
  hour: 'numeric',
  minute: 'numeric',
})
const convertDate = (date) => {
  newDate.value = new Date(date)
  newDate.value = newDate.value.toLocaleString("en-GB", options.value)
  return newDate.value
}

let showDetail = ref(false)
function showDetails() {
  showDetail.value = !showDetail.value
}

let showClosed = ref(false)
let showActive = ref(true)
async function showClosedVal(event) {
  if (event.target.id === 'active') {
    displayedPagesClose = computed(() => {
      const start = Math.max(1, pageClose.value.page + 1 - Math.floor(numButton.value) + 1)
      const end = Math.min(pageClose.value.totalPages, start + numButton.value - 1)
      const pages = []

      for (let i = start; i <= end; i++) {
        pages.push(i)
      }

      return pages
    })
    cate.value = myMode.category
    pageClose.value = await fetchPage(0, "close", myMode.categoryId, 5)
    pageClose.value.content = pageClose.value.content.map(data => {

      if (data.closeDate !== null) {
        data.closeDate = convertDate(data.closeDate)
      }

      return data
    })
    if (pageClose.value.content.length === 0) {
      error.value = 'No Announcements'
      showError.value = true
      showButtonClose.value = false
    }
    if (pageClose.value.content.length !== 0) {
      showError.value = false
      showButtonClose.value = true
    }
    if (pageClose.value.totalElements < 5) {
      showButtonClose.value = false
    }
    if (pageActive.value.content.length === 0) {
      error.value = 'No Announcements'
      showError.value = true
      showButtonActive.value = false
    }
    if (pageActive.value.content.length !== 0) {
      showError.value = false
      showButtonActive.value = true
    }
    if (pageActive.value.totalElements < 5) {
      showButtonActive.value = false
    }

    showActive.value = true
    showClosed.value = false
  }
  if (event.target.id === 'close') {
    displayedPagesActive = computed(() => {
      const start = Math.max(1, pageActive.value.page + 1 - Math.floor(numButton.value) + 1)
      const end = Math.min(pageActive.value.totalPages, start + numButton.value - 1)
      const pages = []

      for (let i = start; i <= end; i++) {
        pages.push(i)
      }

      return pages
    })
    cate.value = myMode.category
    pageActive.value = await fetchPage(0, "active", myMode.categoryId, 5)
    if (pageActive.value.content.length === 0) {
      error.value = 'No Announcements'
      showError.value = true
      showButtonActive.value = false
    }
    if (pageActive.value.content.length !== 0) {
      showError.value = false
      showButtonActive.value = true
    }
    if (pageActive.value.totalElements < 5) {
      showButtonActive.value = false
    }
    if (pageClose.value.content.length === 0) {
      error.value = 'No Announcements'
      showError.value = true
      showButtonClose.value = false
    }
    if (pageClose.value.content.length !== 0) {
      showError.value = false
      showButtonClose.value = true
    }
    if (pageClose.value.totalElements < 5) {
      showButtonClose.value = false
    }
    showClosed.value = true
    showActive.value = false
  }
}


const timeZoneName = ref(Intl.DateTimeFormat().resolvedOptions().timeZone)

let currentComponent = ref('view')
function setCurrentComponent(currentCom) {
  currentComponent.value = currentCom
}

const deleteAnnouncement = async (deleteId) => {
  try {
    const res = await fetch(`${import.meta.env.VITE_ROOT_API}/api/announcements/${deleteId}`, {
      method: 'DELETE'
    })
    if (res.ok) {
      myAnnounce.announcement = myAnnounce.announcement.filter((a) => {
        return a.id !== deleteId
      })

    } else {
      const errorResponse = await res.json();
      alert(`There is an error : ${JSON.stringify(errorResponse)}`);
    }

  }
  catch (err) {
    alert(err)
  }
}

function doNothing() {
  router.go(0)
}

function prevPage(event) {
  if (event.target.id === "prevClose") {
    pageClose.value.page--
  }

  if (event.target.id === "prevActive") {
    pageActive.value.page--
  }

}

function nextPage(event) {
  if (event.target.id === "nextClose") {

    pageClose.value.page++
  }
  if (event.target.id === "nextActive") {
    pageActive.value.page++
  }
}

async function setDetail(event, id) {
  if (event.target.id === 'active') {
    myMode.setMode('active')
    // countActive.value++
    countActive.value = await fetchId(id, true)
    console.log(countActive.value)
  }
  if (event.target.id === 'close') {
    myMode.setMode('close')
    countActive.value = await fetchId(id, true)
  }
}

const signOut = () => {
  localStorage.removeItem("jwtToken");
  localStorage.removeItem("refreshToken");
  router.push({ name: "UserLogin" });
};
</script>
 
<template>
  <div v-if="showActive">
    <div>
      <div class="bg-teal-600 h-20">
        <RouterLink to="/login">
          <button class="btn btn-outline btn-success absolute top-3 right-6 h-13 w-28 my-1"
            v-if="jwtToken == null">LOGIN</button>
        </RouterLink>
        <div v-if="jwtToken !== null">
          <button class="btn btn-outline btn-error absolute top-3 right-6 h-13 w-28 my-1" @click="signOut">SIGN
            OUT</button>
          <RouterLink to="/announcement">
            <button class="btn btn-outline btn-success absolute top-3">Announcement (Viewer)</button>
          </RouterLink>
          <RouterLink to="/admin/announcement">
            <button class="btn btn-outline btn-success absolute top-3 left-52 w-40">Announcement</button>
          </RouterLink>
          <div v-if="isAdmin">
            <RouterLink to="/admin/user">
              <button class="btn btn-outline btn-success absolute top-3 left-80 w-40"
                style="margin-left: 60px;">User</button>
            </RouterLink>
            <RouterLink :to="{ name: 'MatchPwd' }">
              <button class="btn btn-outline btn-success absolute top-3 left-80 w-40" style="margin-left: 228px;">Match
                Password</button>
            </RouterLink>
          </div>
        </div>
      </div>
      <div class="w-full items-center">
        <div class="text-center py-10">
          <h1 class="text-5xl font-bold">SIT Announcement System (SAS)</h1>
          <!-- <div>
            Admin
          </div> -->
        </div>
        <div v-if="!showDetail">
          <p class="ml-5 mr-96 font-bold text-lg place-items-left mb-3">Date/Time shown In Timezone : {{ timeZoneName }}
          </p>
          <RouterLink to="/announcement">
            <button @click="showClosedVal($event)"
              class="btn btn-outline btn-success ann-button absolute top-26 right-6 h-13 w-46" id="close">Closed
              Announcements</button>
            </RouterLink>
            <RouterLink to="/verify" class="ann-button absolute top-26 right-56">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="0.5"
                stroke="none" class="w-14 h-12 stroke-success hover:fill-success">
                <path stroke-linecap="round" stroke-linejoin="round"
                  d="M17.593 3.322c1.1.128 1.907 1.077 1.907 2.185V21L12 17.25 4.5 21V5.507c0-1.108.806-2.057 1.907-2.185a48.507 48.507 0 0111.186 0z" />
              </svg>
            </RouterLink>
        </div>
        <div class=" inline-flex mb-3">
          <p class="ml-5 mr-4 font-bold text-lg place-items-left mb-3 ">Category : </p>
        </div>
        <select v-model="cate"
          class=" appearance-none  w-2/8  inline-flex bg-white border-gray-400 hover:border-gray-500 px-4 py-2 mr-12 rounded-full shadow ann-category-filter"
          ref="selectCategory" id="active" @click="showCategory">
          <option value="ทั้งหมด" selected>ทั้งหมด</option>
          <option v-for="(categorys, index) in category
                " :key="index">{{ categorys.categoryName }}</option>
        </select>

        <div class="inline-flex" v-if="!showDetail">
          <table class="table-fixed w-full">
            <thead class="border bg-teal-100 h-16">
              <tr class="flex justify-between pt-4">
                <th class="order-1 pl-28 text-lg">No.</th>
                <th class="order-2 pr-80 text-lg">Title</th>
                <th class="order-7 pl-96 pr-36 text-lg">Category</th>
              </tr>
            </thead>
          </table>
        </div>
        <h1 class="font-bold text-6xl flex justify-center py-72 text-red-500 sm:text-6xl" v-if="showError">{{ error }}
        </h1>
        <div v-for="(p, index) in pageActive.content" :key="index" class="w-full items-center border py-2"
          v-if="!showDetail">
          <!-- ยังไม่กด -->
          <div class="items-center text-center px-10">
            <div class="inline-flex">
              <table class="w-full table-fixed">
                <tbody>
                  <tr class="ann-item space-between">
                    <td class="text-left pl-20 pr-24 w-px">{{ (pageActive.page * perPage) + index + 1 }}</td>
                    <td class="text-left ann-title">
                      <RouterLink :to="{ name: 'UserDetail', params: { id: p.id } }" @click="setDetail($event, p.id)"
                        id="active">{{ p.announcementTitle }}</RouterLink>
                    </td>
                    <td class="ann-category ml-80  my-4" :class="{
                      'badge badge-info badge-lg text-white': p.announcementCategory === 'ทั่วไป',
                      'badge badge-success badge-lg text-white': p.announcementCategory === 'ทุนการศึกษา',
                      'badge badge-error badge-lg text-white': p.announcementCategory === 'หางาน',
                      'badge badge-warning badge-lg text-white': p.announcementCategory === 'ฝึกงาน'
                    }">{{ p.announcementCategory }}</td>
                    <td class="pl-9 ann-close-date" v-if="showClosed">{{ p.closeDate ? p.closeDate : '-' }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

      </div>
    </div>
    <div class="pagination-container">
      <div class="mt-4 mb-4  ">
        <button class="btn btn-outline btn-accent ann-page-prev" id="prevActive" v-if="showButtonActive"
          :disabled="pageActive.page === 0" @click="pageSwitch(pageActive.page - 1, $event)">Prev</button>
        <button v-for=" (page, index) in displayedPagesActive" :key="page" v-if="showButtonActive"
          @click="pageSwitch(page, $event)" id="active"
          :class="{ 'active': page === pageActive.page + 1, [`ann-page-${index}`]: true }">
          {{ page }}
        </button>
        <button class="btn btn-outline btn-accent ann-page-next" id="nextActive" v-if="showButtonActive"
          :disabled="pageActive.page === pageActive.totalPages - 1"
          @click="pageSwitch(pageActive.page + 1, $event)">Next</button>
      </div>
    </div>
  </div>

  <div v-if="showClosed">
    <div class="bg-teal-600 h-20">
      <RouterLink to="/login">
        <button class="btn btn-outline btn-success absolute top-3 right-6 h-13 w-28 my-1">LOGIN</button>
      </RouterLink>
      <div v-if="jwtToken !== null">
        <button class="btn btn-outline btn-error absolute top-3 right-6 h-13 w-28 my-1" @click="signOut">SIGN
          OUT</button>
        <RouterLink to="/announcement">
          <button class="btn btn-outline btn-success absolute top-3">Announcement (Viewer)</button>
        </RouterLink>
        <RouterLink to="/admin/announcement">
          <button class="btn btn-outline btn-success absolute top-3 left-52 w-40">Announcement</button>
        </RouterLink>
        <div v-if="isAdmin">
          <RouterLink to="/admin/user">
            <button class="btn btn-outline btn-success absolute top-3 left-80 w-40"
              style="margin-left: 60px;">User</button>
          </RouterLink>
          <RouterLink :to="{ name: 'MatchPwd' }">
            <button class="btn btn-outline btn-success absolute top-3 left-80 w-40" style="margin-left: 228px;">Match
              Password</button>
          </RouterLink>
        </div>
      </div>
    </div>
    <div class="text-center py-10">
      <h1 class="text-5xl font-bold">SIT Announcement System (SAS)</h1>
    </div>
    <div>
      <p class="ml-5 mr-96 font-bold text-lg place-items-left mb-3">Date/Time shown In Timezone : {{ timeZoneName }}
      </p>
      <RouterLink to="/announcement">
        <button @click="showClosedVal($event)"
          class="btn btn-outline btn-success ann-button absolute top-26 right-11 h-13 w-46" id="active">Active
          Announcements</button>
      </RouterLink>
    </div>
    <div class=" inline-flex mb-3">
      <p class="ml-5 mr-4 font-bold text-lg place-items-left mb-3 ">Category : </p>
    </div>
    <select v-model="cate"
      class=" appearance-none  w-2/8  inline-flex bg-white border-gray-400 hover:border-gray-500 px-4 py-2 mr-12 rounded-full shadow ann-category-filter"
      ref="selectCategory" id="close" @click="showCategory">
      <option value="ทั้งหมด" selected>ทั้งหมด</option>
      <option v-for="(categorys, index) in category
                " :key="index">{{ categorys.categoryName }}</option>
    </select>
    <div class="inline-flex">
      <table class="table-fixed w-full">
        <thead class="border bg-teal-100 h-16">
          <tr class="flex justify-between pt-4">
            <th class="order-1 pl-28">No.</th>
            <th class="order-2 mr-96">Title</th>
            <th class="order-3 mr-28 pr-2">Close Date</th>
            <th class="order-7 pr-56 mr-3 ">Category</th>
          </tr>
        </thead>
      </table>
    </div>
    <h1 class="font-bold text-6xl flex justify-center py-72 text-red-500 sm:text-6xl" v-if="showError">{{ error }}
    </h1>
    <div v-for="(p, index) in pageClose.content" :key="index" class="w-full items-center border py-2">
      <!-- ยังไม่กด -->
      <div class="items-center text-center px-10">
        <div class="inline-flex">
          <table class="w-full table-fixed">
            <tbody>
              <tr class="ann-item space-between">
                <td class="text-left pl-20 pr-24 w-px">{{ (pageClose.page * perPage) + index + 1 }}</td>
                <td class="text-left ann-title w-96">
                  <RouterLink :to="{ name: 'UserDetail', params: { id: p.id } }" @click="setDetail($event, p.id)"
                    id="close">{{ p.announcementTitle }}</RouterLink>
                </td>
                <td class="ann-close-date w-96 pl-60">{{ p.closeDate ? p.closeDate : '-' }}</td>
                <td class="ann-category ml-14 my-4" :class="{
                  'badge badge-info badge-lg text-white': p.announcementCategory === 'ทั่วไป',
                  'badge badge-success badge-lg text-white': p.announcementCategory === 'ทุนการศึกษา',
                  'badge badge-error badge-lg text-white': p.announcementCategory === 'หางาน',
                  'badge badge-warning badge-lg text-white': p.announcementCategory === 'ฝึกงาน'
                }">{{ p.announcementCategory }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <div class="pagination-container">
      <div class="mt-4 mb-4">
        <button class="btn btn-outline btn-accent ann-page-prev" id="prevClose" v-if="showButtonClose"
          :disabled="pageClose.page === 0" @click="pageSwitch(pageClose.page - 1, $event)">Prev</button>
        <button v-for=" (page, index) in displayedPagesClose" :key="page" v-if="showButtonClose"
          @click="pageSwitch(page, $event)" id="close"
          :class="{ 'active': page === pageClose.page + 1, [`ann-page-${index}`]: true }">
          {{ page }}
        </button>
        <button class="btn btn-outline btn-accent ann-page-next" id="nextClose" v-if="showButtonClose"
          :disabled="pageClose.page === pageClose.totalPages - 1"
          @click="pageSwitch(pageClose.page + 1, $event)">Next</button>
      </div>
    </div>
  </div>
</template>
 
<style scoped>
.pagination-container {
  display: flex;
  justify-content: center;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
}

button {
  margin: 0 5px;
  padding: 5px 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #fff;
  border-radius: 25px;
}

/* .button-p {
  height: 90px;
  border: 0px #fff;
  margin: 0px;
  border-radius: 15px;
  padding-bottom: 50px;
} */

button.active {
  background-color: #ccc;
}
</style>
