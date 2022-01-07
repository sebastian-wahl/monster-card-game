package game.controller;

import game.helper.RepositoryHelper;
import game.http.enums.StatusCodeEnum;
import game.http.models.PackageModel;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.CardBase;
import game.objects.Package;
import game.objects.User;
import game.objects.card.factory.CardFactory;
import game.objects.enums.CardsEnum;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class PackageController extends ControllerBase {

    private static final String ADMIN_SECURITY_TOKEN = "admin-token-1234";

    private static final String ADMIN_ADD_PACKAGE_PARAMETER = "add_admin_package";
    private static final String ADMIN_ADD_PACKAGE_VALUE = "true";

    private static final String BUY_ADMIN_PACKAGE_PARAMETER = "buy_admin_package";
    private static final String BUY_ADMIN_PACKAGE_VALUE = "true";
    private static final String NOT_ENOUGH_COINS_MESSAGE = "Not enough coins for this action.";
    private static final String NO_ADMIN_PACKAGES_ERROR_MESSAGE = "No predefined packages available.";


    public PackageController(Request request, RepositoryHelper repositoryHelper) {
        super(request, repositoryHelper);
    }

    @Override
    public Response doWorkIntern() throws SQLException {
        Response response = new ConcreteResponse();
        if (userRequest.getAuthorizationToken().equals(ADMIN_SECURITY_TOKEN) && this.hasAddAdminPackageUrlParameter()) {
            this.addAdminPackage(response);
        } else {
            Optional<User> userOpt = this.repositoryHelper.getUserRepository().checkTokenAndGetUser(userRequest.getAuthorizationToken());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (user.getCoins() - Package.PACKAGE_COST >= 0) {
                    user.setCoins(user.getCoins() - Package.PACKAGE_COST);
                    Optional<User> userUpdateOpt = this.repositoryHelper.getUserRepository().update(user);
                    if (userUpdateOpt.isPresent()) {
                        if (this.hasBuyAdminPackageUrlParameter()) {
                            // buy admin created package
                            this.buyAdminPackage(response, userUpdateOpt.get());
                        } else {
                            // buy "random" package
                            this.buyRandomPackage(response, userUpdateOpt.get());
                        }
                    }
                } else {
                    response.setContent(NOT_ENOUGH_COINS_MESSAGE);
                    response.setStatus(StatusCodeEnum.SC_400);
                }
            } else {
                response.setStatus(StatusCodeEnum.SC_401);
                response.setContent(WRONG_SECURITY_TOKEN_ERROR_MESSAGE);
            }
        }
        return response;
    }

    private boolean hasAddAdminPackageUrlParameter() {
        return !this.userRequest.getUrl().getUrlParameters().isEmpty() &&
                this.userRequest.getUrl().getUrlParameters().get(ADMIN_ADD_PACKAGE_PARAMETER) != null &&
                this.userRequest.getUrl().getUrlParameters().get(ADMIN_ADD_PACKAGE_PARAMETER).equals(ADMIN_ADD_PACKAGE_VALUE);
    }

    private boolean hasBuyAdminPackageUrlParameter() {
        return !this.userRequest.getUrl().getUrlParameters().isEmpty() &&
                this.userRequest.getUrl().getUrlParameters().get(BUY_ADMIN_PACKAGE_PARAMETER) != null &&
                this.userRequest.getUrl().getUrlParameters().get(BUY_ADMIN_PACKAGE_PARAMETER).equals(BUY_ADMIN_PACKAGE_VALUE);
    }

    private void addAdminPackage(Response response) throws SQLException {
        if (this.userRequest.getModel() instanceof PackageModel) {
            PackageModel packageModel = (PackageModel) this.userRequest.getModel();
            int packageNumber = this.repositoryHelper.getPackageRepository().addAdminPackage();
            if (packageNumber > 0 && packageModel.getPackageCards().size() == Package.PACKAGE_SIZE) {
                List<CardBase> cardsToAdd = packageModel.getPackageCards().stream()
                        .map(cardModel -> {
                            try {
                                return CardFactory.createCard(
                                        CardsEnum.valueOf(cardModel.getName().replace(' ', '_').toUpperCase()),
                                        cardModel.getId());
                            } catch (IllegalArgumentException ex) {
                                // card name not found
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .peek(cardBase -> cardBase.setAdminPackageNumber(packageNumber))
                        .collect(Collectors.toList());

                this.repositoryHelper.getCardRepository().addCards(cardsToAdd);
                response.setContent(new Package(cardsToAdd).toString());
                response.setStatus(StatusCodeEnum.SC_200);
            } else {
                response.setStatus(StatusCodeEnum.SC_500);
            }
        } else {
            response.setStatus(StatusCodeEnum.SC_400);
            response.setContent(WRONG_BODY_MESSAGE);
        }
    }

    private void buyAdminPackage(Response response, User user) throws SQLException {
        int freePackageNumber = this.repositoryHelper.getPackageRepository().getFirstAvailablePackageNumber();
        if (freePackageNumber > 0) {
            List<CardBase> cards = this.repositoryHelper.getCardRepository().getCardsFromAdminPackage(freePackageNumber);
            if (!cards.isEmpty()) {
                if (this.repositoryHelper.getCardRepository().removeCardsFromAdminPackage(freePackageNumber) && this.repositoryHelper.getPackageRepository().setAdminPackageToBought(freePackageNumber)) {
                    Package p = new Package(cards);
                    Optional<User> stackUserOpt = this.repositoryHelper.getStackRepository().addCardsToUserStack(user, cards);
                    if (stackUserOpt.isPresent()) {
                        response.setStatus(StatusCodeEnum.SC_200);
                        response.setContent(p.toString());
                    }
                } else {
                    response.setStatus(StatusCodeEnum.SC_500);
                }
            }
        } else {
            response.setStatus(StatusCodeEnum.SC_400);
            response.setContent(NO_ADMIN_PACKAGES_ERROR_MESSAGE);
        }
    }

    private void buyRandomPackage(Response response, User user) throws SQLException {
        Package pack = new Package();
        // set response to error and change if everything worked out
        response.setStatus(StatusCodeEnum.SC_500);
        this.repositoryHelper.getCardRepository().addCards(pack.cards);
        Optional<User> userAddStackOpt = this.repositoryHelper.getStackRepository().addCardsToUserStack(user, pack);
        if (userAddStackOpt.isPresent()) {
            response.setStatus(StatusCodeEnum.SC_200);
            response.setContent(pack.toString());
        }
    }
}
