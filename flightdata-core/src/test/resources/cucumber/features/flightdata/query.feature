Feature: queries

  Background:
    Given current time is 0
    Given aircraft data storage contains the following data
      | icao24 | make   | model   | owner   |
      | i1     | make-1 | model-1 | owner-1 |
      | i2     |        |         |         |
      | i4     | make-4 | model-4 | owner-4 |

  Scenario: Querying data
    Given flight data cache for job with id="my-job-id" contains the following entries
      | icao24 | callSign | barometricAltitude | onGround |
      | i1     | c1       | 0                  | true     |
      | i2     | c2       | 3000               | false    |
      | i3     |          |                    |          |
      | i4     | c4       | 100                |          |
    Then querying data for job with id="my-job-id" provides the following results updated at 0
      | icao24 | callSign | barometricAltitude | onGround | make   | model   | owner   |
      | i1     | c1       | 0                  | true     | make-1 | model-1 | owner-1 |
      | i2     | c2       | 3000               | false    |        |         |         |
      | i3     |          |                    |          |        |         |         |
      | i4     | c4       | 100                |          | make-4 | model-4 | owner-4 |

  Scenario: Querying data when there are no flights
    Given flight data cache for job with id="my-job-id" is empty
    Then querying data for job with id="my-job-id" provides empty results at time 0

  Scenario: Querying data when there's no data at all
    Given flight data cache does not contain data for job with id="my-job-id"
    Then querying data for job with id="my-job-id" provides no results

  Scenario: Updating data in cache between queries
    Given flight data cache for job with id="my-job-id" contains the following entries
      | icao24 | callSign | barometricAltitude | onGround |
      | i1     | c1       | 0                  | true     |
      | i2     | c2       | 3000               | false    |
      | i3     |          |                    |          |
      | i4     | c4       | 100                |          |
    Given radar provides the following flights for latMax="50.002698", lonMax="50.004198", latMin="49.997300", lonMin="49.995801", maxAltitude="5"
      | icao24 | callSign | barometricAltitude | onGround |
      | i1     | c1       | 2000               | false    |
      | i2     | c2       | 2500               | false    |
      | i4     | c4       | 100                |          |
    When time is advanced to 10
    And scanner is run with the following configuration
      | jobId     | lat | lon | boundaryN | boundaryE | boundaryS | boundaryW | altitudeThreshold |
      | my-job-id | 50  | 50  | 300       | 300       | 300       | 300       | 5                 |
    Then querying data for job with id="my-job-id" provides the following results updated at 10
      | icao24 | callSign | barometricAltitude | onGround | make   | model   | owner   |
      | i1     | c1       | 2000               | false    | make-1 | model-1 | owner-1 |
      | i2     | c2       | 2500               | false    |        |         |         |
      | i3     |          |                    |          |        |         |         |
      | i4     | c4       | 100                |          | make-4 | model-4 | owner-4 |