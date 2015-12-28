(ns avalon.api.games
  (:require [liberator.core :refer [defresource]]
            [liberator.representation :refer [ring-response]]
            [compojure.core :refer [defroutes ANY]]
            [avalon.api.util :as util]
            [avalon.models.games :as games]
            [avalon.models.groups :as groups]
            [avalon.models.people :as people]))

(defresource games-resource
             :available-media-types ["application/json"]
             :allowed-methods [:post]
             :malformed? (util/malformed? ::data)
             :processable? (fn [ctx]
                             (let [group-id (:groupId (::data ctx))]
                               (if (nil? group-id)
                                 true
                                 (groups/exists? group-id))))
             :post! (fn [ctx]
                      (let [data (::data ctx)
                            game (games/create-game (:groupId data))]
                      {::id (:id game)}))
             :handle-created #(identity {:id (::id %)}))

(defresource get-or-put-game [id]
             :available-media-types ["application/json"]
             :allowed-methods [:get :put]
             :exists? (games/exists? id)
             :can-put-to-missing? false
             :malformed? (util/malformed? ::data)
             :processable? (util/update-fields #{:roles :status} ::data)
             :handle-ok (games/display-game (games/get-game id))
             :put! #(games/update-game id (::data %)))

(defresource game-add-person [id]
             :available-media-types ["application/json"]
             :allowed-methods [:post]
             :exists? (games/exists? id)
             :can-post-to-missing? false
             :malformed? (util/malformed? ::data)
             :processable? (util/require-fields [:name] ::data)
             :post! (fn [ctx]
                      (dosync (let [data (::data ctx)
                                    person (people/create-person (:name data))]
                                (games/add-person id person)))))

(defroutes routes
  (ANY "/games" [] games-resource)
  (ANY "/games/:id" [id] (get-or-put-game id))
  (ANY "/games/:id/people" [id] (game-add-person id)))