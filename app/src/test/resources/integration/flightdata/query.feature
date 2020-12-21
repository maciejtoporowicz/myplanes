Feature: queries

  Background:
    Given current time is 0
    Given aircraft data storage contains the following data
      | icao24 | make   | model   | owner   |
      | i1     | make-1 | model-1 | owner-1 |
      | i2     |        |         |         |
      | i4     | make-4 | model-4 | owner-4 |

  Scenario: Querying data
    Given radar provides the following flights for latMax="50.002698", lonMax="50.004198", latMin="49.997300", lonMin="49.995801"
      | icao24 | callSign | barometricAltitude | onGround | longitude | latitude | track |
      | i1     | c1       | 0                  | true     | 5         | 6        | 11    |
      | i2     | c2       | 3000               | false    |           |          | 12.5  |
      | i3     |          |                    |          | 10        | 11       |       |
      | i4     | c4       | 100                |          |           |          |       |
    And scanner is run with the following configuration
      | jobId     | lat | lon | boundaryN | boundaryE | boundaryS | boundaryW | altitudeThreshold |
      | my-job-id | 50  | 50  | 300       | 300       | 300       | 300       | 5000              |
    Then querying data for job with id="my-job-id" provides the following results updated at 0
      | icao24 | callSign | barometricAltitude | onGround | longitude | latitude | track | make   | model   | owner   |
      | i1     | c1       | 0                  | true     | 5         | 6        | 11    | make-1 | model-1 | owner-1 |
      | i2     | c2       | 3000               | false    |           |          | 12.5  |        |         |         |
      | i3     |          |                    |          | 10        | 11       |       |        |         |         |
      | i4     | c4       | 100                |          |           |          |       | make-4 | model-4 | owner-4 |

  Scenario: Querying data when there are no flights
    Given radar provides the following flights for latMax="50.002698", lonMax="50.004198", latMin="49.997300", lonMin="49.995801"
      | icao24 | callSign | barometricAltitude | onGround |
    And scanner is run with the following configuration
      | jobId     | lat | lon | boundaryN | boundaryE | boundaryS | boundaryW | altitudeThreshold |
      | my-job-id | 50  | 50  | 300       | 300       | 300       | 300       | 5000              |
    Then querying data for job with id="my-job-id" provides empty results at time 0

  Scenario: Querying data when there's no data at all
    Then querying data for job with id="my-job-id" provides no results

  Scenario: Updating data in cache between queries
    Given radar provides the following flights for latMax="50.002698", lonMax="50.004198", latMin="49.997300", lonMin="49.995801"
      | icao24 | callSign | barometricAltitude | onGround | longitude | latitude | track |
      | i1     | c1       | 0                  | true     | 1         | 2        | 11    |
      | i2     | c2       | 3000               | false    |           |          | 12    |
      | i3     |          |                    |          | 5         | 6        |       |
      | i4     | c4       | 100                |          |           |          |       |
    And scanner is run with the following configuration
      | jobId     | lat | lon | boundaryN | boundaryE | boundaryS | boundaryW | altitudeThreshold |
      | my-job-id | 50  | 50  | 300       | 300       | 300       | 300       | 5000              |
    And radar provides the following flights for latMax="50.002698", lonMax="50.004198", latMin="49.997300", lonMin="49.995801"
      | icao24 | callSign | barometricAltitude | onGround | longitude | latitude | track |
      | i1     | c1       | 2000               | false    | 5         | 6        | 13    |
      | i2     | c2       | 2500               | false    | 7         | 8        |       |
      | i4     | c4       | 100                |          | 10        | 11       | 14    |
    When time is advanced to 10
    And scanner is run with the following configuration
      | jobId     | lat | lon | boundaryN | boundaryE | boundaryS | boundaryW | altitudeThreshold |
      | my-job-id | 50  | 50  | 300       | 300       | 300       | 300       | 5000              |
    Then querying data for job with id="my-job-id" provides the following results updated at 10
      | icao24 | callSign | barometricAltitude | onGround | longitude | latitude | track | make   | model   | owner   |
      | i1     | c1       | 2000               | false    | 5         | 6        | 13    | make-1 | model-1 | owner-1 |
      | i2     | c2       | 2500               | false    | 7         | 8        |       |        |         |         |
      | i4     | c4       | 100                |          | 10        | 11       | 14    | make-4 | model-4 | owner-4 |