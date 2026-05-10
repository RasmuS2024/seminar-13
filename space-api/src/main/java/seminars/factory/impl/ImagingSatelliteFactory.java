package seminars.factory.impl;

import org.springframework.stereotype.Component;
import seminars.domains.satellites.ImagingSatellite;
import seminars.domains.satellites.params.ImagingSatelliteParam;
import seminars.domains.satellites.Satellite;
import seminars.domains.satellites.params.SatelliteParam;
import seminars.domains.satellites.SatelliteType;
import seminars.exceptions.SpaceOperationException;
import seminars.factory.SatelliteFactory;

@Component
public class ImagingSatelliteFactory implements SatelliteFactory {

    @Override
    public Satellite createSatelliteWithParameter(SatelliteParam param) {
        if (SatelliteType.IMAGE.equals(param.getType())
                && param instanceof ImagingSatelliteParam imagingSatelliteParam) {
            return new ImagingSatellite(
                    imagingSatelliteParam.getName(),
                    imagingSatelliteParam.getBatteryLevel(),
                    imagingSatelliteParam.getResolution()
            );
        }
        throw new SpaceOperationException("Данный тип параметров не поддерживается");
    }

    @Override
    public boolean isSatelliteTypeSupported(SatelliteType type) {
        return SatelliteType.IMAGE.equals(type);
    }
}
