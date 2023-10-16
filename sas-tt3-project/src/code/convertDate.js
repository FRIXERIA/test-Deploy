
const options = {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: 'numeric',
    // timeZoneName: 'short'
  }
let convertDate = (date) => {
    const newDate = new Date(date)
    // console.log(date)
    // console.log(newDate.value)
    newDate= newDate.toLocaleString("en-GB", options)
    return newDate

  }
  export {convertDate}