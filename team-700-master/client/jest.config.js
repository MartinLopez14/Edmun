module.exports = {
    verbose: true,
    moduleFileExtensions: [
        "js",
        "json",
        "vue"
    ],
    transform: {
        ".*\\.(vue)$": "vue-jest",
        "^.+\\.js$": "<rootDir>/node_modules/babel-jest"
    },
    moduleNameMapper: {
        "^@/(.*)$": "<rootDir>/src/$1",
        '\\.(css|less|sass|scss)$': '<rootDir>/src/test/mock/styleMock.js',   //<-- add this line to fix css import problem
    },
    transformIgnorePatterns: [
        'node_modules/(?!(bootstrap-vue)/)', // <-- add this line to fix SyntaxError: Unexpected token import
    ],
    collectCoverage: true,
    collectCoverageFrom: [
        "src/components/*.{js,vue}",
        "src/pages/**/*.{js,vue}",
        "!**/node_modules/**"
    ],
    coverageReporters: [
        "html",
        "text-summary",
        "lcov"
    ],
    testResultsProcessor: "jest-sonar-reporter"
}
