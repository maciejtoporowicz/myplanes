Feature: broadcasting

  Background:
    Given current time is 0

  Scenario: Broadcasting when nothing is in flight cache
    Given radar provides the following flights for latMax="50.002698", lonMax="50.004198", latMin="49.997300", lonMin="49.995801"
      | icao24 | callSign | barometricAltitude | onGround |
      | i1     | c1       | 5000               | false    |
      | i2     | c2       | 3000               |          |
      | i3     | c3       | 0                  | true     |
      | i4     | c4       | 6000               | false    |
    When scanner is run with the following configuration
      | jobId     | lat | lon | boundaryN | boundaryE | boundaryS | boundaryW | altitudeThreshold |
      | my-job-id | 50  | 50  | 300       | 300       | 300       | 300       | 5000              |
    Then the following notification should be sent
      | jobId     | newFlightsCount |
      | my-job-id | 3               |

  Scenario: Broadcasting when no flights are on the radar
    Given radar provides no flights for latMax="50.002698", lonMax="50.004198", latMin="49.997300", lonMin="49.995801"
    When scanner is run with the following configuration
      | jobId     | lat | lon | boundaryN | boundaryE | boundaryS | boundaryW | altitudeThreshold |
      | my-job-id | 50  | 50  | 300       | 300       | 300       | 300       | 5000              |
    Then no notifications should be sent

  Scenario: Broadcasting when some of the flights found on the radar are in cache
    Given radar provides the following flights for latMax="50.002698", lonMax="50.004198", latMin="49.997300", lonMin="49.995801"
      | icao24 | callSign | barometricAltitude | onGround |
      | i3     | c3       | 0                  | true     |
    When scanner is run with the following configuration
      | jobId     | lat | lon | boundaryN | boundaryE | boundaryS | boundaryW | altitudeThreshold |
      | my-job-id | 50  | 50  | 300       | 300       | 300       | 300       | 5000              |
    Then the following notification should be sent
      | jobId     | newFlightsCount |
      | my-job-id | 1               |
    Given radar provides the following flights for latMax="50.002698", lonMax="50.004198", latMin="49.997300", lonMin="49.995801"
      | icao24 | callSign | barometricAltitude | onGround |
      | i1     | c1       | 5000               | false    |
      | i2     | c2       | 3000               |          |
      | i3     | c3       | 0                  | true     |
    When scanner is run with the following configuration
      | jobId     | lat | lon | boundaryN | boundaryE | boundaryS | boundaryW | altitudeThreshold |
      | my-job-id | 50  | 50  | 300       | 300       | 300       | 300       | 5000              |
    Then the following notification should be sent
      | jobId     | newFlightsCount |
      | my-job-id | 2               |

  Scenario: Broadcasting when none the flights found on the radar are in cache
    Given radar provides the following flights for latMax="50.002698", lonMax="50.004198", latMin="49.997300", lonMin="49.995801"
      | icao24 | callSign | barometricAltitude | onGround |
      | i4     | c4       | 0                  | true     |
    When scanner is run with the following configuration
      | jobId     | lat | lon | boundaryN | boundaryE | boundaryS | boundaryW | altitudeThreshold |
      | my-job-id | 50  | 50  | 300       | 300       | 300       | 300       | 5000              |
    Then the following notification should be sent
      | jobId     | newFlightsCount |
      | my-job-id | 1               |
    Given radar provides the following flights for latMax="50.002698", lonMax="50.004198", latMin="49.997300", lonMin="49.995801"
      | icao24 | callSign | barometricAltitude | onGround |
      | i1     | c1       | 5000               | false    |
      | i2     | c2       | 3000               |          |
      | i3     | c3       | 0                  | true     |
    When scanner is run with the following configuration
      | jobId     | lat | lon | boundaryN | boundaryE | boundaryS | boundaryW | altitudeThreshold |
      | my-job-id | 50  | 50  | 300       | 300       | 300       | 300       | 5000              |
    Then the following notification should be sent
      | jobId     | newFlightsCount |
      | my-job-id | 3               |