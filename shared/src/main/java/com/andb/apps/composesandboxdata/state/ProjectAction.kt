package com.andb.apps.composesandboxdata.state

import com.andb.apps.composesandboxdata.model.PrototypeComponent
import com.andb.apps.composesandboxdata.model.PrototypeTree
import com.andb.apps.composesandboxdata.model.Theme
import kotlinx.serialization.Serializable

@Serializable
sealed class ProjectAction {
    @Serializable data class ExtractComponent(val oldComponent: PrototypeComponent) : ProjectAction()
    @Serializable data class DeleteTree(val tree: PrototypeTree) : ProjectAction()
    @Serializable data class UpdateTheme(val theme: Theme) : ProjectAction()
    @Serializable data class UpdateName(val name: String) : ProjectAction()
    @Serializable sealed class TreeAction : ProjectAction() {
        @Serializable data class AddComponent(val adding: PrototypeComponent, val parent: PrototypeComponent.Group, val indexInParent: Int) : TreeAction()
        @Serializable data class MoveComponent() : TreeAction()
        @Serializable data class UpdateComponent(val component: PrototypeComponent) : TreeAction()
        @Serializable data class DeleteComponent(val component: PrototypeComponent) : TreeAction()
    }
}

@Serializable
sealed class ProjectActionResidual {
    @Serializable data class ExtractComponent(val action: ProjectAction.ExtractComponent) : ProjectActionResidual()
    @Serializable data class DeleteTree(val action: ProjectAction.DeleteTree, val replacementIDs: List<String>) : ProjectActionResidual()
    @Serializable data class UpdateTheme(val oldTheme: Theme) : ProjectActionResidual()
    @Serializable data class UpdateName(val oldName: String) : ProjectActionResidual()
    @Serializable sealed class TreeActionResidual : ProjectActionResidual() {
        @Serializable data class AddComponent(val adding: PrototypeComponent) : TreeActionResidual()
        @Serializable data class MoveComponent() : TreeActionResidual()
        @Serializable data class UpdateComponent(val oldComponent: PrototypeComponent) : TreeActionResidual()
        @Serializable data class DeleteComponent(val component: PrototypeComponent, val parent: PrototypeComponent.Group, val indexInParent: Int) : TreeActionResidual()
    }
}