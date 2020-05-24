package com.coditory.mongo.reader

import spock.lang.Specification
import spock.lang.Unroll

import static com.coditory.mongo.reader.CsvParser.parseCsvRow

class CsvParserTest extends Specification {
    @Unroll
    def "should parse csv row: #row"() {
        when:
            List<String> result = parseCsvRow(row)
        then:
            result == expected
        where:
            row                                | expected
            "a,b,c"                            | List.of("a", "b", "c")
            "a, b, c"                          | List.of("a", "b", "c")
            " a , b , c "                      | List.of("a", "b", "c")
            " abc,  d e f ,gh i  "             | List.of("abc", "d e f", "gh i")
            " a , , c "                        | List.of("a", "", "c")
            "  , ,   "                         | List.of("", "", "")
            " \" abc , def \",\"\", \"   \"  " | List.of(" abc , def ", "", "   ")
            " ' abc , def ','', '   '  "       | List.of(" abc , def ", "", "   ")
    }
}
