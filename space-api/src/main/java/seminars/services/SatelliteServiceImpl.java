package seminars.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seminars.domains.satellites.Satellite;
import seminars.domains.satellites.SatelliteParam;
import seminars.exceptions.SpaceOperationException;
import seminars.factory.SatelliteFactory;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SatelliteServiceImpl implements SatelliteService {
    private final List<SatelliteFactory> factories;

    @Override
    public Satellite createSatellite(SatelliteParam param) {
        if (param == null) {
            throw new SpaceOperationException("Параметры спутника не могут быть null");
        }
        if (param.getType() == null) {
            throw new SpaceOperationException("Тип спутника не может быть null");
        }

        SatelliteFactory factory = factories.stream()
                .filter(satelliteFactory -> satelliteFactory
                        .isSatelliteTypeSupported(param.getType()))
                .findFirst()
                .orElseThrow(() -> new SpaceOperationException("Данный тип параметров не поддерживается"));

        return factory.createSatelliteWithParameter(param);
    }
}
