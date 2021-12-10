import { onMount } from "svelte"

type KVValue = any

interface NetworkKVUpdate {
    k: string
    v: KVValue
}

export type UpdateListener = (key: string, value: KVValue) => void
export type ConnectionListener = (connected: boolean) => void

export class Entry {
    private readonly key: string
    private readonly kv: NetworkKV

    constructor(kv: NetworkKV, key: string) {
        this.kv = kv
        this.key = key
    }

    public get(key: string): Entry {
        return new Entry(this.kv, `${this.key}/${key}`)
    }

    public addListener(listener: UpdateListener): VoidFunction {
        return this.kv.addListener(this.key, listener)
    }

    public string(): string {
        return this.kv.getRaw(this.key) as string
    }

    public boolean(): boolean {
        return this.kv.getRaw(this.key) as boolean
    }
}

// export class KVStore {
//
// }

export default class NetworkKV {
    private static instance: NetworkKV

    private ws!: WebSocket
    private table: Map<String, any> = new Map()
    private listeners: Map<String, Set<UpdateListener>> = new Map()
    private connectionListeners: Set<ConnectionListener> = new Set()

    constructor(port: number) {
        console.log("nt constructor")

        let createSocket = () => {
            console.log("create socket")
            this.ws = new WebSocket(
                `${location.protocol == "https:" ? "wss:" : "ws:"}//${
                    location.hostname
                }:${location.port}/ws`,
            )
            // this.ws = new WebSocket(
            //     `ws://localhost/ws`,
            // )

            this.ws.onmessage = (event) => {
                let data: NetworkKVUpdate = JSON.parse(event.data)
                this.table.set(data.k, data.v)
                console.log("got key: " + data.k)
                this.listeners.get(data.k)?.forEach((listener) => {
                    listener(data.k, data.v)
                })
            }

            this.ws.onopen = (_event) => {
                this.connectionListeners.forEach((listener) => {
                    listener(true)
                })
            }

            this.ws.onclose = (_event) => {
                this.connectionListeners.forEach((listener) => {
                    listener(false)
                })

                setTimeout(createSocket, 1000)
            }
        }

        createSocket()
    }

    static getDefault(): NetworkKV {
        if (NetworkKV.instance === undefined) {
            NetworkKV.instance = new NetworkKV(8887)
        }
        return NetworkKV.instance
    }

    public put(key: string, value: KVValue): void {
        this.table.set(key, value)
        if (this.ws.readyState !== WebSocket.OPEN) {
            return
        }
        this.ws.send(JSON.stringify({ k: key, v: value }))
    }

    public get(key: string): Entry {
        return new Entry(this, this.getRaw(key))
    }

    public getRaw(key: string): any {
        return this.table.get(key)
    }

    public addListener(
        key: string,
        listener: UpdateListener,
        initial: boolean = false,
    ): VoidFunction {
        let listeners = this.listeners.get(key)
        if (listeners == null) {
            listeners = new Set()
            this.listeners.set(key, listeners)
        }

        listeners.add(listener)

        // if (initial) {
        //     listener(key, this.table.get(key))
        // }

        return () => {
            console.log("dropping listener for " + key)
            this.listeners.get(key)?.delete(listener)
        }
    }

    public addConnectionListener(listener: ConnectionListener): VoidFunction {
        this.connectionListeners.add(listener)

        return () => {
            this.connectionListeners.delete(listener)
        }
    }
}
