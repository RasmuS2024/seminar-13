package seminars.factory.impl;

import org.springframework.stereotype.Component;
import seminars.domains.satellites.*;
import seminars.exceptions.SpaceOperationException;
import seminars.factory.SatelliteFactory;

@Component
public class CommunicationSatelliteFactory implements SatelliteFactory {

    @Override
    public Satellite createSatelliteWithParameter(SatelliteParam param) {
        if (SatelliteType.COMMUNICATION.equals(param.getType())
                && param instanceof CommunicationSatelliteParam communicationSatelliteParam) {
            return new CommunicationSatellite(
                    communicationSatelliteParam.getName(),
                    communicationSatelliteParam.getBatteryLevel(),
                    communicationSatelliteParam.getBandwidth()
            );
        }
        throw new SpaceOperationException("Данный тип параметров не поддерживается");
    }

    @Override
    public boolean isSatelliteTypeSupported(SatelliteType type) {
        return SatelliteType.COMMUNICATION.equals(type);
    }

}