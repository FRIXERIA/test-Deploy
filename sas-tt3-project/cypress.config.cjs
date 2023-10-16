const { defineConfig } = require("cypress");

module.exports = defineConfig({
  e2e: {
   specPattern: "cypress/e2e/**/*.{cy,spec}.{js,jsx,ts,tsx}",
  //  baseUrl:"http://localhost:5173/",
   baseUrl:"https://intproj22.sit.kmutt.ac.th/tt3/",
   experimentalSessionAndOrigin: true
  },
});
