export namespace NetworkTables {
    function putValue(key: string, value: any): void

    function getValue(key: string, defaultValue?: any): any

    type UnsubscribeListener = () => void
    type KeyListener = (key: string, value: any, isNew: boolean) => void

    function addGlobalListener(
        callback: KeyListener,
        immediateNotify: boolean = false,
    ): UnsubscribeListener

    function addKeyListener(
        key: string,
        callback: KeyListener,
        immediateNotify: boolean = false,
    ): UnsubscribeListener

    type BooleanCallback = (connected: boolean) => boid

    function addRobotConnectionListener(
        callback: BooleanCallback,
        immediateNotify: boolean = false,
    )

    function addWsConnectionListener(
        callback: BooleanCallback,
        immediateNotify: boolean = false,
    )
}
